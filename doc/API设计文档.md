# API

> 以下是自己实现过程中头脑风暴乱写的记录( ´(00)`)，后面改进成为 API 文档。

maybe private_key can be load in the request url, or just stay in the request json

maybe just use /api/dosomething as url

use **POSTMAN** for testing!!!

## register

### request

```json
{
  "balance": "<int>"
}
```

### response

```json
{
  "status": "success/fail",
  "private_key": "<abcd>",
  "address": "<0xabcd>"
}
```

_if refresh, check vuex for state, if null just go to login page_



## get_user_info

### request

```json
{
  "private_key": "<abcd>"
}
```

### response

```json
{
  "status": "success/fail",
  "address": "<0xabcd>",
  "balance": "<int>",
  "credit": "<int>"
}
```

_also used to signin_



## transfer_money

### request

```json
{
  "private_key": "<abcd>",
  "recipient": "<address 0xabcd>",
  "amount": "<int>"
}
```

### response

```json
{
  "status": "success/fail",
  "balance": "<update int>"
}
```



## loan

### request

```json
{
  "private_key": "<abcd>",
  "recipient": "<address 0xabcd>",
  "amount": "<int>",
  "due_day": "<day_num int>"
}
```

### response

```json
{
  "status": "success/fail",
  "balance": "<update int>"
}
```



## get_loans

### request

```json
{
  "private_key": "<abcd>"
}
```

### response

```json
{
  "status": "success/fail",
  "loans": [
    {
      "loan_index": "<int>",
      "loan_ID": "<1234>",
      "is_lender": "<t/f>",
      "other": "<address 0xabcd>",
      "amount": "<int>",
      "due_date": "<timestamp>",
      "is_closed": "<t/f>"
    },
    {
      "...": "..."
    }
  ]
}
```

_if isClosed, show 'closed/finished', else if isLender, button show 'transfer', else show 'pay'_

_index stored but not show, used to pay loan_

_index in front-end are the same as in chain-end and back-end_



## pay_loan

### request

```json
{
  "private_key": "<abcd>",
  "loan_index": "<int>",
  "amount": "<int>"
}
```

### response

```json
{
  "status": "success/fail",
  "balance": "<update int>",
  "credit": "<update int>"
}
```

_on success, front-end just reload get\_loans to refresh_



## transfer_loan

### request

```json
{
  "private_key": "<abcd>",
  "loan_index": "<int>",
  "recipient": "<address 0xabcd>",
  "amount": "<int>"
}
```

### response

```json
{
  "status": "success/fail",
  "balance": "<update int>"
}
```

_on success, front-end just reload get\_loans to refresh_



## financing

### request

```json
{
  "private_key": "<abcd>",
  "amount": "<int>",
  "due_day": "<day_num int>"
}
```

### response

```json
{
  "status": "success/fail",
  "balance": "<update int>"
}
```

_on success, front-end just reload get\_loans to refresh_