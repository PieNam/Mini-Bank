<template>
  <div id="wrapper">
    <h1 id="register-title">- Register -</h1>
    <form onsubmit="return false;">
      <label for="info-balance"><div :class="placeholder_div">Your Initial Balance</div></label>
      <input class="info-getter focus" @focus="setPlaceHolder" @blur="setPlaceHolderBack" id="info-balance" v-model="balance" type="text" />
      <button id="submit-btn" :disabled="button_status" type="submit" @click="register">confirm</button>
    </form>
    <p id="bottom-info">Already have an account? <a @click="jumpToSignIn">Sign In</a></p>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data () {
    return {
      balance: '',
      placeholder_div: 'placeholder'
    }
  },
  computed: {
    button_status: function () {
      return !(this.balance.length)
    }
  },
  methods: {
    setPlaceHolder () {
      this.placeholder_div = 'placeholder placeholder_active'
    },
    setPlaceHolderBack () {
      if (this.balance.length === 0) {
        this.placeholder_div = 'placeholder'
      }
    },
    register () {
      var pattern = /^[0-9]{1,20}$/
      if (!pattern.exec(this.balance)) {
        this.$Message['warning']({background: true, content: 'Invalid Balance Input!'})
      } else {
        var registerinfo = {
          'balance': this.balance
        }
        this.axios.post('/api/register', registerinfo).then(res => {
          console.log(res)
          if (res.data.status === 'success') {
            let successcontent = '<ul>Please take care of your own private key and address<li>private key: <p style="word-wrap:break-word;">' + res.data.private_key + '</p></li><li>address: ' + res.data.address + '</li></ul>'
            this.$Modal.success({
              title: 'Register Success',
              content: successcontent,
              onOk: () => {
                this.jumpToSignIn()
              }})
          } else {
            this.$Modal.error({
              title: 'Register failed',
              content: '<p>Please check the environment.</p>'})
          }
        }).catch(err => {
          console.log('error: ' + err)
        })
      }
    },
    jumpToSignIn () {
      this.$router.push({
        path: '/signin'
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
