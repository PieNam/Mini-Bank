import Vue from 'vue'
import Router from 'vue-router'
import Register from '@/components/Register'
import SignIn from '@/components/SignIn'
import Home from '@/components/Home'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Index',
      component: SignIn,
      meta: {
        title: 'Mini-Bank | Sign In'
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: {
        title: 'Mini-Bank | Register'
      }
    },
    {
      path: '/signin',
      name: 'SignIn',
      component: SignIn,
      meta: {
        title: 'Mini-Bank | Sign In'
      }
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      meta: {
        title: 'Mini-Bank | Home'
      }
    }
  ]
})
