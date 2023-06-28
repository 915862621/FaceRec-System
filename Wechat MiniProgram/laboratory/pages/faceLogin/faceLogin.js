// pages/faceLogin/faceLogin.js
const app = getApp();
Page({
  data: {
    text: '\n'
  },

  record: function () {
    this.ctx = wx.createCameraContext()
    var that = this

    that.ctx.takePhoto({
      quality: 'high',

      success: (res) => {
        that.isExistFace(that, res.tempImagePath)
      }
    })
  },

  isExistFace: function (that, imgPath) {
    wx.uploadFile({
      url: 'http://192.168.43.51:9090//AppletsController/isExistFace',
      filePath: imgPath,
      name: 'file',
      success: function (res) {
        if (JSON.parse(res.data).code == 200) {
          that.setData({
            text: "眨眨眼"
          })
          that.eye(that)
        } else {
          that.setData({
            text: "未检测到人脸"
          })
        }
      }
    })
  },

  eye: function (that) {
    var interval = setInterval(() => {
      that.ctx.takePhoto({
        quality: 'high',

        success: (res) => {
          wx.uploadFile({
            url: 'http://192.168.43.51:9090//AppletsController/isWink',
            filePath: res.tempImagePath,
            name: 'file',
            success: function (res) {
              if (JSON.parse(res.data).code == 200) {
                that.mouth(that)
                clearInterval(interval);
              }
            }
          })
        }
      })
    }, 150)
  },

  mouth: function (that) {
    setTimeout(() => {
      that.setData({
        text: "张张嘴"
      })

      var interval = setInterval(() => {
        that.ctx.takePhoto({
          quality: 'high',

          success: (res) => {
            wx.uploadFile({
              url: 'http://192.168.43.51:9090//AppletsController/isOpenMouth',
              filePath: res.tempImagePath,
              name: 'file',
              success: function (res) {
                if (JSON.parse(res.data).code == 200) {
                  that.rec(that)
                  clearInterval(interval);
                }
              }
            })
          }
        })
      }, 300)
    }, 500)
  },

  rec: function (that) {
    setTimeout(() => {
      that.setData({
        text: "正在验证中"
      })

      that.ctx.takePhoto({
        quality: 'high',

        success: (res) => {
          wx.uploadFile({
            url: 'http://192.168.43.51:9090/AppletsController/Rec',
            filePath: res.tempImagePath,
            name: 'file',
            formData: {
              'account': wx.getStorageSync('unitName')
            },
            success: function (res) {
              if (JSON.parse(res.data).code == 200) {
                wx.setStorageSync('unitId', JSON.parse(res.data).obj.id);
                wx.setStorageSync('portrait', 'http://192.168.43.51:9090' + JSON.parse(res.data).obj.portrait);
                wx.showToast({
                  title: '欢迎您 ' + wx.getStorageSync('unitName'),
                  icon: 'success',
                  duration: 1000
                })
                setTimeout(() => {
                  wx.switchTab({
                    url: '../home/home',
                  })
                }, 1500)
              } else {
                wx.showToast({
                  title: '验证失败 请重试',
                  icon: 'error',
                  duration: 1500
                })
              }
            }
          })
        }
      })
    }, 500)
  }
})