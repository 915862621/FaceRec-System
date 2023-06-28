#include <dlib/dnn.h>
#include <dlib/image_io.h>
#include <dlib/misc_api.h>

using namespace dlib;
using namespace std;


// 读取数据集目录
std::vector<std::vector<string>> load_objects_list (
    const string& dir 
)
{
    std::vector<std::vector<string>> objects;
    for (auto subdir : directory(dir).get_dirs())
    {
        std::vector<string> imgs;
        for (auto img : subdir.get_files())
            imgs.push_back(img);

        if (imgs.size() != 0)
            objects.push_back(imgs);
    }
    return objects;
}

// 从数据集随机选取样本作为一个batch
void load_mini_batch (
    const size_t num_people,
    const size_t samples_per_id,
    dlib::rand& rnd,
    const std::vector<std::vector<string>>& objs,
    std::vector<matrix<rgb_pixel>>& images,
    std::vector<unsigned long>& labels
)
{
    images.clear();
    labels.clear();

    std::vector<bool> already_selected(objs.size(), false);
    matrix<rgb_pixel> image; 
    for (size_t i = 0; i < num_people; ++i)
    {
        size_t id = rnd.get_random_32bit_number()%objs.size();
        while(already_selected[id])
            id = rnd.get_random_32bit_number()%objs.size();
        already_selected[id] = true;

        for (size_t j = 0; j < samples_per_id; ++j)
        {
            const auto& obj = objs[id][rnd.get_random_32bit_number()%objs[id].size()];
            load_image(image, obj);
            images.push_back(std::move(image));
            labels.push_back(id);
        }
    }
}


// 残差结构定义
template <
    int N, 
    template <typename> class BN, 
    int stride, 
    typename SUBNET> 
using block  = BN<
                con<N,3,3,1,1,
                relu<BN<
                con<N,3,3,stride,stride,SUBNET>>>>>;

template <
    template <int,template<typename>class,int,typename> class block, 
    int N, 
    template<typename>class BN, 
    typename SUBNET>
using residual = add_prev1<
                    block<N,BN,1,
                    tag1<SUBNET>>>;

template <
    template <int,template<typename>class,int,typename> class block, 
    int N, 
    template<typename>class BN, 
    typename SUBNET>
using residual_down = add_prev2<
                        avg_pool<2,2,2,2,skip1<
                        tag2<block<N,BN,2,
                        tag1<SUBNET>>>>>>;

template <int N, typename SUBNET> 
using res = relu<residual<block,N,bn_con,SUBNET>>;
template <int N, typename SUBNET> 
using res_down = relu<residual_down<block,N,bn_con,SUBNET>>;

template <int N, typename SUBNET> using ares      = relu<residual<block,N,affine,SUBNET>>;
template <int N, typename SUBNET> using ares_down = relu<residual_down<block,N,affine,SUBNET>>;


// 残差网络定义
template <typename SUBNET> 
using level0 = res_down<256,SUBNET>;
template <typename SUBNET> 
using level1 = res<256,res<256,res_down<256,SUBNET>>>;
template <typename SUBNET> 
using level2 = res<128,res<128,res_down<128,SUBNET>>>;
template <typename SUBNET> 
using level3 = res<64,res<64,res<64,res_down<64,SUBNET>>>>;
template <typename SUBNET> 
using level4 = res<32,res<32,res<32,SUBNET>>>;

template <typename SUBNET> using alevel0 = ares_down<256,SUBNET>;
template <typename SUBNET> using alevel1 = ares<256,ares<256,ares_down<256,SUBNET>>>;
template <typename SUBNET> using alevel2 = ares<128,ares<128,ares_down<128,SUBNET>>>;
template <typename SUBNET> using alevel3 = ares<64,ares<64,ares<64,ares_down<64,SUBNET>>>>;
template <typename SUBNET> using alevel4 = ares<32,ares<32,ares<32,SUBNET>>>;


using net_type = loss_metric<fc_no_bias<128,avg_pool_everything<
                            level0<
                            level1<
                            level2<
                            level3<
                            level4<
                            max_pool<3,3,2,2,relu<bn_con<con<32,7,7,2,2,
                            input_rgb_image_sized<150>
                            >>>>>>>>>>>>;

using anet_type = loss_metric<fc_no_bias<128,avg_pool_everything<
                            alevel0<
                            alevel1<
                            alevel2<
                            alevel3<
                            alevel4<
                            max_pool<3,3,2,2,relu<affine<con<32,7,7,2,2,
                            input_rgb_image_sized<150>
                            >>>>>>>>>>>>;

// ----------------------------------------------------------------------------------------

int main(int argc, char** argv)
{
    // 命令参数说明
    if (argc != 2)
    {
        cout << "Give a folder as input.  It should contain sub-folders of images and we will " << endl;
        cout << "learn to distinguish between these sub-folders with metric learning.  " << endl;
        cout << "For example, you can run this program on the very small examples/johns dataset" << endl;
        cout << "that comes with dlib by running this command:" << endl;
        cout << "   ./dnn_metric_learning_on_images_ex johns" << endl;
        return 1;
    }





    auto objs = load_objects_list(argv[1]);
    std::vector<matrix<rgb_pixel>> images;
    std::vector<unsigned long> labels;
    net_type net;

    // 设置训练参数
    dnn_trainer<net_type> trainer(net, sgd(0.0001, 0.9));
    trainer.set_learning_rate(0.1);
    trainer.be_verbose();
    trainer.set_synchronization_file("face_metric_sync", std::chrono::minutes(5));
    trainer.set_iterations_without_progress_threshold(300);

    dlib::pipe<std::vector<matrix<rgb_pixel>>> qimages(4);
    dlib::pipe<std::vector<unsigned long>> qlabels(4);
    auto data_loader = [&qimages, &qlabels, &objs](time_t seed)
    {
        dlib::rand rnd(time(0)+seed);
        std::vector<matrix<rgb_pixel>> images;
        std::vector<unsigned long> labels;
        while(qimages.is_enabled())
        {
            try
            {
                load_mini_batch(5, 5, rnd, objs, images, labels);
                qimages.enqueue(images);
                qlabels.enqueue(labels);
            }
            catch(std::exception& e)
            {
                cout << "EXCEPTION IN LOADING DATA" << endl;
                cout << e.what() << endl;
            }
        }
    };
    std::thread data_loader1([data_loader](){ data_loader(1); });
    std::thread data_loader2([data_loader](){ data_loader(2); });
    std::thread data_loader3([data_loader](){ data_loader(3); });
    std::thread data_loader4([data_loader](){ data_loader(4); });
    std::thread data_loader5([data_loader](){ data_loader(5); });

    // 训练到学习率小于10的-4次方
    while(trainer.get_learning_rate() >= 1e-4)
    {
        qimages.dequeue(images);
        qlabels.dequeue(labels);
        trainer.train_one_step(images, labels);
    }

    // 保存模型
    trainer.get_net();
    cout << "done training" << endl;
    net.clean();
    serialize("metric_network_renset.dat") << net;

    qimages.disable();
    qlabels.disable();
    data_loader1.join();
    data_loader2.join();
    data_loader3.join();
    data_loader4.join();
    data_loader5.join();





    dlib::rand rnd(time(0));
    load_mini_batch(5, 5, rnd, objs, images, labels);
    anet_type testing_net = net;
    
    std::vector<matrix<float,0,1>> embedded = testing_net(images);

    int num_right = 0;
    int num_wrong = 0;

    // 模型评估
    for (size_t i = 0; i < embedded.size(); ++i)
    {
        for (size_t j = i+1; j < embedded.size(); ++j)
        {
            if (labels[i] == labels[j])
            {
                if (length(embedded[i]-embedded[j]) < testing_net.loss_details().get_distance_threshold())
                    ++num_right;
                else
                    ++num_wrong;
            }
            else
            {
                if (length(embedded[i]-embedded[j]) >= testing_net.loss_details().get_distance_threshold())
                    ++num_right;
                else
                    ++num_wrong;
            }
        }
    }

    cout << "num_right: "<< num_right << endl;
    cout << "num_wrong: "<< num_wrong << endl;
}