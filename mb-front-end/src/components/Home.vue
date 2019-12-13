<template>
  <div id="wrapper">
    <div id="user-wrapper">
      <Divider id="user-title">User Detail</Divider>
      <div id="user-subwrapper">
        <img id="avatar-img" src="../assets/avatar.png">
        <h3>- My Account -</h3>
        <Divider><span style="font-size:12px">Details</span></Divider>
        <h3>Balance: {{balance}}</h3>
        <h3>Credit : {{credit}}</h3>
        <h3 style="word-wrap:break-word;text-decoration:underline;">Address: <span style="font-size:8px">{{address}}</span></h3>
        <Divider dashed><span style="font-size:12px">Operations</span></Divider>
        <div class="opr-wrapper" id="first-opr" @click="transfer">
          <img src="../assets/transfer.png" title="Transfer Money" class="opr-icon">
          <span>Transfer</span>
        </div>
        <div class="opr-wrapper" @click="loan">
          <img src="../assets/loan.png" title="Loan to others" class="opr-icon">
          <span>Loan</span>
        </div>
        <div class="opr-wrapper" @click="financing">
          <img src="../assets/financing.png" title="Issue Financing" class="opr-icon">
          <span>Financing</span>
        </div>
      </div>
    </div>
    <div id="loan-wrapper">
      <Divider id="loan-table-title">Loans Detail</Divider>
      <Table
        border
        size="small"
        height="500"
        :loading="loading_loans"
        :columns="loans_column"
        :data="loans_data"
        no-data-text="No Loan Yet"></Table>
    </div>
  </div>
</template>

<script>
import ExpandRow from './Table-Expand.vue'

export default {
  name: 'Home',
  components: {
    ExpandRow
  },
  data () {
    return {
      private_key: '',
      address: '',
      balance: 0,
      credit: 0,
      loading_loans: false,
      loans_column: [
        {
          type: 'expand',
          width: 50,
          render: (h, params) => {
            return h(ExpandRow, {
              props: {
                row: params.row
              }
            })
          }
        },
        {
          title: 'Loan ID',
          key: 'loan_ID'
        },
        // { // to expand
        //   title: 'Peer Address',
        //   key: 'other'
        // },
        {
          title: 'Amount',
          key: 'amount'
        },
        // { // to expand
        //   title: 'Finish At',
        //   key: 'due_date'
        // },
        {
          title: 'Status',
          key: 'is_closed'
        },
        {
          title: 'Operation',
          key: 'operation',
          width: '100',
          render: (h, params) => {
            if (params.row.is_lender) {
              return h('Button', {
                domProps: {
                  innerHTML: 'Transfer'
                },
                class: 'opr-btn',
                style: 'width:70px',
                on: {
                  click: () => {
                    event.stopPropagation()
                    this.transferLoan(params.row.loan_index)
                  }
                }
              })
            } else if (params.row.other === '0xad3171dfe936edf74c7d5959aeff29607da3f3e0') {
              return h('Button', {
                domProps: {
                  innerHTML: 'Fund'
                },
                class: 'opr-btn',
                style: 'width:70px'
              })
            } else {
              return h('Button', {
                domProps: {
                  innerHTML: 'Pay'
                },
                class: 'opr-btn',
                style: 'width:70px',
                on: {
                  click: () => {
                    event.stopPropagation()
                    this.payLoan(params.row.loan_index)
                  }
                }
              })
            }
          }
        }
      ],
      loans_data: []
    }
  },
  computed: {
  },
  methods: {
    doSomethingFailed (item) {
      this.$Message['error']({
        background: true,
        content: item + ' Failed! Please Sign In Again...',
        duration: 2,
        onClose: () => {
          this.jumpToSignIn()
        }})
    },
    loadInfo () {
      this.loading_loans = true
      var info = {
        'private_key': this.private_key
      }
      this.axios.post('/api/get_user_info', info).then(res => {
        console.log('get user info: ', res)
        if (res.data.status === 'success' && res.data.address === this.address) {
          this.balance = res.data.balance
          this.credit = res.data.credit
          this.loadLoans()
        } else {
          this.doSomethingFailed('Load Balance and Credit')
        }
      }).catch(err => {
        console.log('error: ' + err)
        this.doSomethingFailed('Load Balance and Credit')
      })
    },
    timestampHandler (timestamp) {
      var date = new Date(timestamp)
      var year = date.getFullYear()
      var month = date.getMonth() + 1
      var day = date.getDate()
      return year + '-' + month + '-' + day
    },
    loadLoans () {
      var info = {
        'private_key': this.private_key
      }
      this.axios.post('/api/get_loans', info).then(res => {
        console.log('get loans: ', res)
        if (res.data.status === 'success') {
          this.loans_data = res.data.loans
          console.log('raw loans_data: ', this.loans_data)
          for (let i = 0; i < this.loans_data.length; ++i) {
            this.loans_data[i].due_date = this.timestampHandler(this.loans_data[i].due_date)
            this.loans_data[i].is_closed = this.loans_data[i].is_closed ? 'Closed' : 'Open'
          }
          console.log('cooked loans_data: ', this.loans_data)
          this.loading_loans = false
        } else {
          this.doSomethingFailed('Load Loans')
        }
      }).catch(err => {
        console.log('error: ' + err)
        this.doSomethingFailed('Load Loans')
      })
    },
    transfer () {
      this.$Modal.confirm({
        render: (h) => {
          return [
            h('Input', {
              props: {
                value: '',
                autofocus: true,
                clearable: true,
                placeholder: 'address of the recipient'
              },
              on: {
                input: (val) => {
                  this.$store.state.transfer_info.recipient = val
                }
              }
            }),
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'amount to transfer'
              },
              on: {
                input: (val) => {
                  this.$store.state.transfer_info.amount = val
                }
              }
            })
          ]
        },
        onOk: () => {
          var addresspattern = /^0[xX][0-9a-fA-F]+$/
          var amountpattern = /^[0-9]{1,20}$/
          if (!addresspattern.exec(this.$store.state.transfer_info.recipient) || !amountpattern.exec(this.$store.state.transfer_info.amount)) {
            this.$Message['error']({background: true, content: 'Invalid Transfer Information Input!'})
          } else {
            var transferinfo = {
              'private_key': this.private_key,
              'recipient': this.$store.state.transfer_info.recipient,
              'amount': this.$store.state.transfer_info.amount
            }
            this.axios.post('/api/transfer_money', transferinfo).then(res => {
              console.log('transfer money:', res)
              if (res.data.status === 'success' && res.data.balance > 0) {
                this.$Message['success']({background: true, content: 'Transfer Success! Please check your balance.'})
                this.balance = res.data.balance
              } else {
                this.$Message['error']({background: true, content: 'Transfer Failed!'})
              }
            }).catch(err => {
              console.log('error: ' + err)
              this.getInfoFailed('Issue Transfer')
            })
          }
          this.$store.state.transfer_info.recipient = ''
          this.$store.state.transfer_info.amount = ''
        }
      })
    },
    loan () {
      this.$Modal.confirm({
        render: (h) => {
          return [
            h('Input', {
              props: {
                value: '',
                autofocus: true,
                clearable: true,
                placeholder: 'address of the recipient'
              },
              on: {
                input: (val) => {
                  this.$store.state.loan_info.recipient = val
                }
              }
            }),
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'amount to loan'
              },
              on: {
                input: (val) => {
                  this.$store.state.loan_info.amount = val
                }
              }
            }),
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'time limit of the loan (days)'
              },
              on: {
                input: (val) => {
                  this.$store.state.loan_info.due_day = val
                }
              }
            })
          ]
        },
        onOk: () => {
          var addresspattern = /0[xX][0-9a-fA-F]{40}/
          var amountpattern = /^[0-9]{1,20}$/
          var daypattern = /^[0-9]{1,20}$/
          if (!addresspattern.exec(this.$store.state.loan_info.recipient) || !amountpattern.exec(this.$store.state.loan_info.amount) || !daypattern.exec(this.$store.state.loan_info.due_day)) {
            this.$Message['error']({background: true, content: 'Invalid Loan Information Input!'})
          } else {
            var loaninfo = {
              'private_key': this.private_key,
              'recipient': this.$store.state.loan_info.recipient,
              'amount': this.$store.state.loan_info.amount,
              'due_day': this.$store.state.loan_info.due_day
            }
            this.axios.post('/api/loan', loaninfo).then(res => {
              console.log('loan:', res)
              if (res.data.status === 'success' && res.data.balance > 0) {
                this.$Message['success']({background: true, content: 'Loan Success! Please check your balance and loans.'})
                this.balance = res.data.balance
                this.loadLoans()
              } else {
                this.$Message['error']({background: true, content: 'Loan Failed!'})
              }
            }).catch(err => {
              console.log('error: ' + err)
              this.getInfoFailed('Issue Loan')
            })
          }
          this.$store.state.loan_info.recipient = ''
          this.$store.state.loan_info.amount = ''
          this.$store.state.loan_info.due_day = ''
        }
      })
    },
    financing () {
      this.$Modal.confirm({
        render: (h) => {
          return [
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'amount to financing'
              },
              on: {
                input: (val) => {
                  this.$store.state.financing_info.amount = val
                }
              }
            }),
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'time limit of the financing (days)'
              },
              on: {
                input: (val) => {
                  this.$store.state.financing_info.due_day = val
                }
              }
            })
          ]
        },
        onOk: () => {
          var amountpattern = /^[0-9]{1,20}$/
          var daypattern = /^[0-9]{1,20}$/
          if (!amountpattern.exec(this.$store.state.financing_info.amount) || !daypattern.exec(this.$store.state.financing_info.due_day)) {
            this.$Message['error']({background: true, content: 'Invalid Financing Information Input!'})
          } else {
            var financinginfo = {
              'private_key': this.private_key,
              'amount': this.$store.state.financing_info.amount,
              'due_day': this.$store.state.financing_info.due_day
            }
            this.axios.post('/api/financing', financinginfo).then(res => {
              console.log('financing:', res)
              if (res.data.status === 'success' && res.data.balance > 0) {
                this.$Message['success']({background: true, content: 'Financing Success! Please check your balance and loans.'})
                this.balance = res.data.balance
                this.loadLoans()
              } else {
                this.$Message['error']({background: true, content: 'Financing Failed!'})
              }
            }).catch(err => {
              console.log('error: ' + err)
              this.getInfoFailed('Issue Financing')
            })
          }
          this.$store.state.financing_info.amount = ''
          this.$store.state.financing_info.due_day = ''
        }
      })
    },
    transferLoan (index) {
      console.log('transfer loan at index:' + index)
      this.$Modal.confirm({
        render: (h) => {
          return [
            h('Input', {
              props: {
                value: '',
                autofocus: true,
                clearable: true,
                placeholder: 'address of the recipient'
              },
              on: {
                input: (val) => {
                  this.$store.state.transfer_loan_info.recipient = val
                }
              }
            }),
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'amount of the loan to transfer'
              },
              on: {
                input: (val) => {
                  this.$store.state.transfer_loan_info.amount = val
                }
              }
            })
          ]
        },
        onOk: () => {
          var addresspattern = /0[xX][0-9a-fA-F]{40}/
          var amountpattern = /^[0-9]{1,20}$/
          if (!addresspattern.exec(this.$store.state.transfer_loan_info.recipient) || !amountpattern.exec(this.$store.state.transfer_loan_info.amount)) {
            this.$Message['error']({background: true, content: 'Invalid Transfer Loan Information Input!'})
          } else {
            var transferloaninfo = {
              'private_key': this.private_key,
              'loan_index': index,
              'recipient': this.$store.state.transfer_loan_info.recipient,
              'amount': this.$store.state.transfer_loan_info.amount
            }
            this.axios.post('/api/transfer_loan', transferloaninfo).then(res => {
              console.log(res)
              if (res.data.status === 'success' && res.data.balance > 0) {
                this.$Message['success']({background: true, content: 'Transfer Loan Success! Please check your balance and loans.'})
                this.balance = res.data.balance
                this.loadLoans()
              } else {
                this.$Message['error']({background: true, content: 'Transfer Loan Failed!'})
              }
            }).catch(err => {
              console.log('error: ' + err)
              this.getInfoFailed('Issue Transfer Loan')
            })
          }
          this.$store.state.transfer_loan_info.recipient = ''
          this.$store.state.transfer_loan_info.amount = ''
        }
      })
    },
    payLoan (index) {
      console.log('pay loan at index:' + index)
      this.$Modal.confirm({
        render: (h) => {
          return [
            h('Input', {
              props: {
                value: '',
                number: true,
                clearable: true,
                placeholder: 'amount to pay, not exceed the total amount'
              },
              on: {
                input: (val) => {
                  this.$store.state.pay_loan_info.amount = val
                }
              }
            })
          ]
        },
        onOk: () => {
          var amountpattern = /^[0-9]{1,20}$/
          if (!amountpattern.exec(this.$store.state.pay_loan_info.amount)) {
            this.$Message['error']({background: true, content: 'Invalid Pay Loan Information Input!'})
          } else {
            var payloaninfo = {
              'private_key': this.private_key,
              'loan_index': index,
              'amount': this.$store.state.pay_loan_info.amount
            }
            this.axios.post('/api/pay_loan', payloaninfo).then(res => {
              console.log(res)
              if (res.data.status === 'success' && res.data.balance > 0) {
                this.$Message['success']({background: true, content: 'Pay Loan Success! Please check your balance and loans.'})
                this.balance = res.data.balance
                this.credit = res.data.credit
                this.loadLoans()
              } else {
                this.$Message['error']({background: true, content: 'Pay Loan Failed!'})
              }
            }).catch(err => {
              console.log('error: ' + err)
              this.getInfoFailed('Issue Pay Loan')
            })
          }
          this.$store.state.pay_loan_info.recipient = ''
          this.$store.state.pay_loan_info.amount = ''
        }
      })
    },
    jumpToSignIn () {
      this.$router.push({
        path: '/signin'
      })
    }
  },
  created () {
    if (!this.$store.state.user_private_key || !this.$store.state.user_address) {
      this.doSomethingFailed('Private Key and Address')
    } else {
      this.private_key = this.$store.state.user_private_key
      this.address = this.$store.state.user_address
      document.title = this.$route.meta.title
      this.loadInfo()
    }
  }
}
</script>

<style lang="css" scoped>
* {
  transition: 0.5s;
}
#wrapper {
  position: relative;
  margin: auto;
  margin-top: -10px;
  width: 810px;
  left: 0;
  right: 0;
  border-radius: 20px;
}
#user-wrapper {
  width: 200px;
  height: 400px;
  float: left;
}
#user-subwrapper {
  background-color: white;
  width: 200px;
  height: 400px;
  float: left;
}
#user-subwrapper h3 {
  position: relative;
  margin: auto;
  margin-bottom: 5px;
  left: 0;
  right: 0;
  max-width: 200px;
}
#avatar-img {
  width: 60px;
  margin-top: 20px;
}
.opr-wrapper {
  width: 100%;
  height: 50px;
  border-top: 1px transparent solid;
  border-image: linear-gradient(to right, white, #e0e0e0, white);
  border-image-slice: 10;
  text-align: left;
  padding-left: 50px;
  background-color: white;
}
.opr-wrapper:first {
  border: none;
}
.opr-wrapper:hover {
  background-color: #f5f5f5;
}
.opr-icon {
  margin-top: 12px;
  margin-right: 20px;
  width: 20px;
  vertical-align: bottom;
}
.opr-icon span {
  font-size: 16px;
  color: #555555;
}
#first-opr {
  border: none;
  margin-top: -20px;
}
.opr-btn {
  width: 80px;
}
#loan-wrapper {
  background-color: #f0f0f0;
  width: 600px;
  height: 600px;
  margin-left: 210px;
  overflow: hidden;
}
#loan-table-title {
  font-size: 20px;
}
#user-title {
  font-size: 20px;
}
</style>
