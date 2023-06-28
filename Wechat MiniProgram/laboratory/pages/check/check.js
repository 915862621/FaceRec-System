const app = getApp()

Page({
  data: {
    password: ''
  },

  // 获取输入密码 
  passwordInput: function (e) {
    this.setData({
      password: e.detail.value
    })
  },

  login: function () {
    var that = this;
    if (this.data.password.length == 0) {
      wx.showToast({
        title: '密码不能为空',
        icon: 'none',
        duration: 1500
      })
    } else {
      wx.request({
        url: 'http://192.168.43.51:9090/AppletsController/login',
        method: 'post',
        data: {
          account: wx.getStorageSync('unitName'),
          password: that.data.password
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded' // 默认值
        },
        success(res) {
          if (res.data.code == "200") {
            wx.showToast({
              title: '密码正确',
              icon: 'success',
              duration: 1500,
              success: function () {
                setTimeout(function () {
                  wx.navigateTo({
                    url: '../face/face',
                  })
                }, 1500);
              }
            })


          } else {
            wx.showToast({
              title: '密码错误',
              icon: 'error',
              duration: 1500
            })
          }
        }
      })
    }
  }
})