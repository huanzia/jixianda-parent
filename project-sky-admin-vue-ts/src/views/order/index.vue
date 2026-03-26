<template>
  <OrderDetails />
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import OrderDetails from '@/views/orderDetails/index.vue'

@Component({
  components: {
    OrderDetails
  }
})
export default class extends Vue {}
</script>