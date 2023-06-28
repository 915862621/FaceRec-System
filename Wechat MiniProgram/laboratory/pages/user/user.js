//user.js
//获取应用实例
var app = getApp()
Page({
  data: {
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  onLoad: function () {
    var that = this;
    that.setData({
      nickName: wx.getStorageSync('unitName'),
      userInfo: wx.getStorageSync('portrait'),
      hasUserInfo: true
    })
  },

  // 退出登录
  logoff: function (event) {
    wx.removeStorageSync('unitId');
    wx.removeStorageSync('unitName')
    wx.removeStorageSync('portrait')

    this.setData({
      userInfo: null,
      hasUserInfo: false,
    })

    wx.redirectTo({
      url: '../index/index',
    })
  },

  //更换头像
  bindViewTap: function () {
    var that = this;
    wx.chooseImage({
      count: 1, // 默认9     
      sizeType: ['original', 'compressed'],
      // 指定是原图还是压缩图，默认两个都有     
      sourceType: ['album', 'camera'],
      // 指定来源是相册还是相机，默认两个都有   
      success: function (res) {
        //console.log("头像更换"+res.tempFilePaths);
        wx.uploadFile({
          url: 'http://192.168.43.51:9090/AppletsController/updataportrait',
          filePath: res.tempFilePaths[0],
          name: 'file',
          formData: {
            // wx.getStorageSync('unitName')
            'account': wx.getStorageSync('unitName'),
            'id': wx.getStorageSync('unitId')
          },
          success: function (res) {
            //console.log("返回值"+JSON.parse(res.data).obj);
            var pictureUrl = 'http://192.168.43.51:9090' + JSON.parse(res.data).obj;
            //console.log("图片路径"+pictureUrl);
            if (JSON.parse(res.data).code == 200) {
              wx.setStorageSync('portrait', pictureUrl);
              that.setData({
                userInfo: pictureUrl,
              })
              wx.showToast({
                title: '头像保存成功',
                icon: 'success',
                duration: 200
              })
            } else {
              wx.showToast({
                title: '头像保存失败',
                image: '../../images/myUser/error.png',
                duration: 200
              })
            }
          }
        })
      }
    })
  },

  faceClick: function () {
    wx.navigateTo({
      url: '../check/check',
    })
  }

  // faceCheck: function () {
  //   //检查人员是否允许进入
  //   wx.request({
  //     url: 'http://192.168.43.51:9090/AppletsController/login',
  //     method: 'post',
  //     data: {
  //       id: wx.getStorageSync('unitId'),
  //       account: wx.getStorageSync('unitName')
  //     },
  //     header: {
  //       'content-type': 'application/x-www-form-urlencoded' // 默认值
  //     },
  //     success(res) {
  //       if (res.data.code == "200") {
  //         wx.navigateTo({
  //           url: '../face/face',
  //         })
  //       } else {
  //         wx.showToast({
  //           title: "密码错误",
  //           icon: 'none',
  //           duration: 1500
  //         })
  //       }
  //     }
  //   })
  // }
})