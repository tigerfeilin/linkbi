<template>
  <el-form-item :label="$t('chart.order')">
    <draggable v-model="orderByStrs" :group="{name: 'orderBy',pull: false, put: false}" style="display: inline-block;">
      <el-tag v-for="(item,index) in orderByStrs" :key="index" closable size="small" @close="handleCloseOrderBy(item)">
        {{ item }}
      </el-tag>
    </draggable>
    <el-cascader v-model="orderBy" :options="orderByOption" :key="cascaderKey" :disabled="orderByOption.length===0" size="mini" :placeholder="$t('chart.selectOrderBy')" style="width: 150px;" @change="handleOrderByChange" />
  </el-form-item>
</template>
<script>
import draggable from 'vuedraggable'

export default {
  components: { draggable },
  props: {
    value: {
      required: true,
      type: Array
    }
  },
  data() {
    return {
      cascaderKey:0,
      orderBy: []
    }
  },
  computed: {
    caculCols() {
      return this.$store.state.chart.caculCols
    },
    dimensions() {
      return this.$store.state.chart.dimensions
    },
    allSelected() {
      return this.dimensions.concat(this.caculCols)
    },
    orderByStrs: {
          set(value) {
              this.$emit('input', value)
          },
          get() {
              const orderByStrs = [...this.value]
              orderByStrs.forEach((orderByStr, index) => {
                  const colName = orderByStr.split(' ')[0]
                  if (this.allSelected.findIndex(col => col.Column === colName) < 0) {
                      orderByStrs.splice(index, 1)
                  }
              })
              return orderByStrs
          }
    },
    orderByOption() {
        ++this.cascaderKey
        if(this.allSelected !== undefined && this.allSelected.length > 0)
        {
            return this.allSelected.map(col => {
                return {
                    value: col.Column,
                    label: col.Column,
                    children: [{
                        value: 'desc',
                        label: this.$t('chart.descend')
                    }, {
                        value: 'asc',
                        label: this.$t('chart.ascend')
                    }]
                }
            })
        }
        else
            return []
    }
  },
  methods: {
    handleOrderByChange(value) {
      this.orderBy = []
      const index = this.orderByStrs.findIndex(orderBy => orderBy.indexOf(value[0]) >= 0)
      if (index >= 0) {
          this.orderByStrs.splice(index, 1, `${value[0]} ${value[1]}`)
      } else {
          this.orderByStrs.push(`${value[0]} ${value[1]}`)
      }
      this.orderByStrs = this.orderByStrs
    },
    handleCloseOrderBy(value) {
        this.orderByStrs.splice(this.orderByStrs.indexOf(value), 1)
        this.orderByStrs = this.orderByStrs//.splice(this.orderByStrs.indexOf(value), 1)
    }
  }
}
</script>
