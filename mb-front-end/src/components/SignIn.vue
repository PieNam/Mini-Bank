<template>
  <div id="wrapper">
    <h1 id="register-title">- Sign In -</h1>
    <form onsubmit="return false;">
      <label for="info-privatekey"><div :class="placeholder_div">Your Private Key</div></label>
      <input class="info-getter focus" @focus="setPlaceHolder" @blur="setPlaceHolderBack" id="info-privatekey" v-model="private_key" type="text" />
      <button id="submit-btn" :disabled="button_status" type="submit" @click="signin">confirm</button>
    </form>
    <p id="bottom-info">Have no account yet? <a @click="jumpToRegister">Register</a></p>
  </div>
</template>

<script>
export default {
  name: 'SignIn',
  data () {
    return {
      private_key: '',
      placeholder_div: 'placeholder'
    }
  },
  computed: {
    button_status: function () {
      return !(this.private_key.length)
    }
  },
  methods: {
    setPlaceHolder () {
      this.placeholder_div = 'placeholder placeholder_active'
    },
    setPlaceHolderBack () {
      if (this.private_key.length === 0) {
        this.placeholder_div = 'placeholder'
      }
    },
    signin () {
      console.log('signin')
      var pattern = /[a-fA-F0-9]{64}/
      if (!pattern.exec(this.private_key)) {
        console.log('error!')
        this.$Message['warning']({background: true, content: 'Invalid Private Key Input!'})
      } else {
        var signinfo = {
          'private_key': this.private_key
        }
        this.axios.post('/api/get_user_info', signinfo).then(res => {
          console.log(res)
          if (res.data.status === 'success' && res.data.address.length > 10) {
            this.$store.state.user_private_key = this.private_key
            this.$store.state.user_address = res.data.address
            this.$Message['success']({
              background: true,
              content: 'Sign In Success!',
              duration: 2,
              onClose: () => {
                this.jumpToHome()
              }})
          } else {
            this.$Message['error']({background: true, content: 'Sign In failed! Please check your private key.'})
          }
        }).catch(err => {
          console.log('error: ' + err)
          this.$Message['error']({background: true, content: 'Sign In failed! Please check your private key.'})
        })
      }
    },
    jumpToHome () {
      this.$router.push({
        path: '/home'
      })
    },
    jumpToRegister () {
      this.$router.push({
        path: '/register'
      })
    }
  },
  created () {
    document.title = this.$route.meta.title
  }
}
</script>

<style lang="css" scoped>
* {
  transition: 0.5s;
}
#wrapper {
  position: relative;
  background-color: white;
  width: 500px;
  height: 500px;
  margin: auto;
  left: 0;
  right: 0;
  border-radius: 20px;
}
#register-title {
  position: relative;
  top: 60px;
  margin-bottom: 150px;
}
.info-getter {
  display: block;
  outline: none;
  font-size: 20px;
  width: 300px;
  height: 20px;
  margin: auto;
  left: 0;
  right: 0;
  padding: 15px 5px;
  margin-bottom: 50px;
  border: none;
  border-bottom: 2px gray solid;
  background: none;
}
.info-getter:focus {
  border-bottom: 2px black solid;
}
#submit-btn {
  color: white;
  font-size: 15px;
  font-weight: bold;
  width: 300px;
  height: 50px;
  margin-top: 20px;
  border: none;
  outline: none;
  background: linear-gradient(120deg, #555555, black, #555555);
  background-size: 200%;
  transition: 0.5s;
}
#submit-btn:hover {
  background-position: right;
}
.placeholder {
  z-index: 1;
  width: 300px;
  font-size: 18px;
  color: gray;
  position: relative;
  top: 20px;
  left: 100px;
  text-align: left;
}
.placeholder_active {
  color: black;
  top: -10px;
}
#bottom-info {
  font-size: 14px;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 50px;
}
#bottom-info a {
  color: skyblue;
  cursor: pointer;
}
</style>
