// pages/register/register.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    account: '',
    password: '',
    passwordConfirm: ''
  },

  // 获取输入账号 
  accountInput: function (e) {
    this.setData({
      account: e.detail.value
    })
  },

  // 获取输入密码 
  passwordInput: function (e) {
    this.setData({
      password: e.detail.value
    })
  },
  passwordInputConfirm: function (e) {
    this.setData({
      passwordConfirm: e.detail.value
    })
  },


  onregisterClick: function () {
    var that = this;
    if (this.data.account.length == 0 || this.data.password.length == 0) {
      wx.showToast({
        title: '账号或密码不能为空',
        icon: 'none',
        duration: 1500
      })
    } else if (this.data.password != this.data.passwordConfirm) {
      wx.showToast({
        title: '两次密码不相同',
        icon: 'none',
        duration: 1500
      })
    } else {
      wx.request({
        url: 'http://192.168.43.51:9090/AppletsController/register',
        method: 'post',
        data: {
          account: that.data.account,
          password: that.data.password
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        success(res) {
          if (res.data.code == "200") {
            var unitId = res.data.obj.id;
            var unitName = res.data.obj.account;
            wx.setStorageSync('unitId', unitId);
            wx.setStorageSync('unitName', unitName);
            wx.setStorageSync('portrait', 'http://192.168.43.51:9090' + res.data.obj.portrait);
            wx.navigateTo({
              url: '../face/face',
            })
          } else {
            wx.showToast({
              title: res.data.message,
              icon: 'none',
              duration: 1500
            })
          }
        }
      })
    }
  }
})