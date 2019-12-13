import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    user_private_key: '',
    user_address: '',
    transfer_info: {
      recipient: '',
      amount: ''
    },
    loan_info: {
      recipient: '',
      amount: '',
      due_day: ''
    },
    financing_info: {
      amount: '',
      due_day: ''
    },
    transfer_loan_info: {
      recipient: '',
      amount: ''
    },
    pay_loan_info: {
      amount: ''
    }
  }
})

export default store
