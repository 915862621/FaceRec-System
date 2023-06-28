import json
import cv2
import numpy as np
from flask import request, Flask
import dlib
from imutils import face_utils


# 读取人脸检测器
detector = dlib.get_frontal_face_detector()
# 读取人脸关键点检测模型
sp = dlib.shape_predictor('../../../models/my_dlib_shape_predictor.dat')
# 读取人脸识别模型
facerec = dlib.face_recognition_model_v1('../../../models/metric_network_renset.dat')


def eye_aspect_ratio(eye):
    A = np.linalg.norm(np.array(eye[1]) - np.array(eye[5]))
    B = np.linalg.norm(np.array(eye[2]) - np.array(eye[4]))
    C = np.linalg.norm(np.array(eye[0]) - np.array(eye[3]))
    ear = (A + B) / (2.0 * C)
    return ear


def mouth_aspect_ratio(mouth):
    A = np.linalg.norm(np.array(mouth[2]) - np.array(mouth[10]))
    B = np.linalg.norm(np.array(mouth[4]) - np.array(mouth[8]))
    C = np.linalg.norm(np.array(mouth[0]) - np.array(mouth[6]))
    mar = (A + B) / (2.0 * C)
    return mar


app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False


# python转换为json对象的转换规则
class NpEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        elif isinstance(obj, np.floating):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        else:
            return super(NpEncoder, self).default(obj)


@app.route('/faceapi', methods=['POST'])
def post_Data():

    # 面部128维编码计算并返回
    if (request.json['flag'] == "true"):
        # 获取图片存储路径
        data = request.json['url']
        # 获取用户名
        username = request.json['username']
        # 进行解析

        # 添加用户到列表中
        username_list = [username]
        face_encodings = []

        # 计算面部编码
        img = dlib.load_rgb_image(data)
        dets = detector(img, 1)
        for k, d in enumerate(dets):
            shape = sp(img, d)
            face_encoding = list(facerec.compute_face_descriptor(img, shape))

        # 添加人脸编码添加到列表中
        face_encodings.append(face_encoding)
        # 进行用户和人脸编码映射
        face_dic = dict(map(lambda x, y: [x, y], username_list, face_encodings))

        # 把python对象转换为json对象
        json_face = json.dumps(face_dic, cls=NpEncoder)
        return json_face

    # 判断图像内是否检测到人脸
    if (request.json['flag'] == "isExistFace"):
        # 获取图片路径
        data = request.json['url']

        # 读取图片
        img = dlib.load_rgb_image(data)
        # 人脸检测器进行检测
        dets = detector(img, 1)
        # 判断检测结果长度，如果非零表示检测到了人脸
        if len(dets) > 0:
            return 'true'
        return 'false'

    # 眨眼检测
    if (request.json['flag'] == "isWink"):
        # 首先获取双眼的序号
        (lStart, lEnd) = face_utils.FACIAL_LANDMARKS_IDXS['left_eye']
        (rStart, rEnd) = face_utils.FACIAL_LANDMARKS_IDXS['right_eye']

        # 获取图片路径并读取为灰度图
        data = request.json['url']
        frame = cv2.imread(data)
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        predictor = sp

        # 检测人脸所在位置
        rects = detector(gray, 0)
        for rect in rects:
            # 获得68个关键点
            shape = predictor(gray, rect)
            shape = face_utils.shape_to_np(shape)

            # 计算左右眼的眼睑/眼角比例
            leftEye = shape[lStart:lEnd]
            rightEye = shape[rStart:rEnd]
            leftEAR = eye_aspect_ratio(leftEye)
            rightEAR = eye_aspect_ratio(rightEye)
            ear = (leftEAR + rightEAR) / 2.0
            print("ear：{}".format(ear))

            # 通过眼睑/眼角比例判断是否进行了眨眼动作
            if ear < 0.23:
                return 'true'
            else:
                return 'false'

    # 张嘴检测（流程与眨眼检测一致）
    if (request.json['flag'] == "isOpenMouth"):
        (mStart, mEnd) = (49, 68)

        data = request.json['url']
        frame = cv2.imread(data)
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        predictor = sp
        rects = detector(gray, 1)

        for rect in rects:
            shape = predictor(gray, rect)
            shape = face_utils.shape_to_np(shape)

            mouth = shape[mStart:mEnd]
            mar = mouth_aspect_ratio(mouth)
            print("mar：{}".format(mar))

            if mar > 0.75:
                return 'true'
            else:
                return 'false'

    # 计算两个128维面部编码的向量距离，根据距离判断是否是请求登录的用户本人
    if (request.json['flag'] == "Rec"):
        # data记录当前正在进行刷脸操作的用户的实时图片
        data = request.json['url']
        # face记录数据库中存储的128维面部编码
        face = request.json['face']
        face = list(map(float, face.split("[")[1].split("]")[0].split(", ")))

        # 读取实时图片，计算编码
        img = dlib.load_rgb_image(data)
        dets = detector(img, 1)
        for k, d in enumerate(dets):
            shape = sp(img, d)
            coding = facerec.compute_face_descriptor(img, shape)

        # 通过计算2范数，得到两个面部编码的向量距离
        match = np.linalg.norm(np.array(face) - np.array(coding))
        print("距离：" + str(match))


        if match < 0.5:
            return 'true'
        else:
            return 'false'


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=9900)
