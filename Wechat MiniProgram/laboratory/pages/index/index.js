const app = getApp()

Page({
  // 获取输入账号 
  usernameInput: function (e) {
    this.setData({
      username: e.detail.value
    })
  },

  // 获取输入密码 
  // passwordInput: function (e) {
  //   this.setData({
  //     password: e.detail.value
  //   })
  // },

  register: function () {
    wx.navigateTo({
      url: '../register/register',
    })
  },

  // 登录处理
  login: function () {
    if (this.data.username.length == 0) {
      wx.showToast({
        title: '账号不能为空',
        icon: 'none',
        duration: 1500
      })
    } else {
      wx.setStorageSync('unitName', this.data.username)
      wx.navigateTo({
        url: '../faceLogin/faceLogin',
      })
      // wx.request({
      //   url: 'http://192.168.43.51:9090/AppletsController/login',
      //   method: 'post',
      // data: {
      //   account: that.data.username,
      //   password: that.data.password
      // },
      //   header: {
      //     'content-type': 'application/x-www-form-urlencoded' // 默认值
      //   },
      //   success(res) {
      //     console.log(res)
      //     if (res.data.code == "200") {
      //       var unitName = res.data.obj.account;
      //       var unitId = res.data.obj.id;
      //       var entryaccess = res.data.obj.entryaccess;
      //       wx.setStorageSync('unitId', unitId);
      //       wx.setStorageSync('unitName', unitName);
      //       wx.setStorageSync('portrait', 'http://192.168.43.51:9090' + res.data.obj.portrait);
      //       wx.setStorageSync('entryaccess', entryaccess);
      //       wx.showToast({
      //         title: '登录成功',
      //         icon: 'success',
      //         duration: 1500,
      // success: function () {
      //   setTimeout(function () {
      //     wx.switchTab({
      //       url: '../door/door'
      //     })
      //   }, 1500);
      // }
      //       })
      //     } else {
      //       wx.showToast({
      //         title: res.data.message,
      //         icon: 'none',
      //         duration: 1500
      //       })
      //     }
      //   }
      // })
    }
  }
})