/* eslint-disable */
<template>
  <div>
    <Row class="expand-row">
      <Col span="16">
        <span class="expand-key">Peer Address: </span>
        <div class="expand-value">{{ row.other }}</div>
      </Col>
      <Col span="8">
        <span class="expand-key">Finish At: </span>
        <div class="expand-value">{{ row.due_date }}</div>
      </Col>
    </Row>
  </div>
</template>

<script>
export default {
  props: {
    row: Object
  }
}
</script>

<style scoped>
.expand-row {
  margin-bottom: 5px;
}
.expand-key {
  display: inline-block;
  font-weight: 600;
}
.expand-value {
  text-decoration: underline;
}
</style>
