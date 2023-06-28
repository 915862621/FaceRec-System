// pages/face/face.js
const app = getApp();
Page({
  record: function () {
    let that = this
    let ctx = wx.createCameraContext()

    ctx.takePhoto({
      quality: 'high',

      success: (res) => {
        that.localhostimgesupdata(res.tempImagePath)
      }
    })
  },

  localhostimgesupdata: function (imgPath) {
    wx.uploadFile({
      url: 'http://192.168.43.51:9090//AppletsController/appUpdate',
      filePath: imgPath,
      name: 'file',
      formData: {
        'id': wx.getStorageSync('unitId'),
        'account': wx.getStorageSync('unitName')
      },
      success: function (res) {
        if (JSON.parse(res.data).code == 200) {
          wx.showToast({
            title: '人脸保存成功',
            icon: 'success',
            duration: 1500,
            success: function () {
              setTimeout(function () {
                wx.navigateBack({
                  delta: 2  // 返回上级页面。
                })
              }, 1500);
            }
          })
        } else {
          wx.showToast({
            title: '人脸保存失败',
            icon: 'error',
            duration: 1500
          })
        }
      }
    })
  }
})