<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="执行器名称" prop="searchVal">
        <el-input
          v-model="listQuery.registryKey"
          clearable
          placeholder="请输入执行器名称"
          style="width: 240px;"
          size="small"
          @keyup.enter.native="fetchData"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="fetchData">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"

      highlight-current-row
    >
      <el-table-column label="执行器"  align="center">
        <template slot-scope="scope">{{ scope.row.registryKey }}</template>
      </el-table-column>
      <el-table-column label="注册地址"  align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.registryValue }}</template>
      </el-table-column>
      <el-table-column label="更新时间"  align="center">
        <template slot-scope="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="CPU使用率(%)"  align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.cpuUsage > 90.0" style="color: red">{{ scope.row.cpuUsage }} </span>
          <span v-else >{{ scope.row.cpuUsage }} </span>
        </template>
      </el-table-column>
      <el-table-column label="内存使用率(%)" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.memoryUsage > 90.0" style="color: red">{{ scope.row.memoryUsage }} </span>
          <span v-else >{{ scope.row.memoryUsage }} </span>
        </template>
      </el-table-column>
      <el-table-column label="平均负载" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.loadAverage }}</template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script>
import { getList } from '@/api/executor/registry'
import waves from '@/directive/waves' // waves directive
import echarts from 'echarts'
export default {
  name: 'Registry',
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'gray',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        registryKey: undefined
      },
      dialogPluginVisible: false
    }
  },
  created() {
    this.fetchData()
  },
  mounted() {

  },
  methods: {
    fetchData() {
      this.listLoading = true
      this.list = []
      getList(this.listQuery).then(response => {
        const { records } = response.data
        const { total } = response.data
        this.total = total
        this.list = records
        this.listLoading = false
        //  console.log(this.list);
        //this.$nextTick(function() {
        //  for (let i = 0; i < this.list.length; i++) {
        //    this.initEcharts(this.list[i])
        //  }
        //})
      })
    },
    initEcharts(data) {
      const myChart1 = echarts.init(document.getElementsByClassName(data.id)[0])
      // 绘制图表
      var option1 = {
        title: {
          text: 'cpu使用率',
          subtext: '',
          x: 'center'
        },
        tooltip: {
          formatter: '{a} <br/>{b} : {c}%'
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {}
          },
          show: false
        },
        series: [{
          name: 'cpu使用率',
          type: 'gauge',
          max: 100,
          radius: '70%', // 半径
          startAngle: 215, // 起始位置
          endAngle: -35, // 终点位置
          detail: {
            formatter: '{value}%'
          },
          data: [{
            value: data.cpuUsage,
            name: ''
          }]
        }]
      }
      myChart1.setOption(option1)

      const myChart2 = echarts.init(document.getElementsByClassName(data.id)[1])
      // 绘制图表
      var option2 = {
        title: {
          text: '内存使用率',
          subtext: '',
          x: 'center'
        },
        tooltip: {
          formatter: '{a} <br/>{b} : {c}%'
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {}
          },
          show: false
        },
        series: [{
          name: '内存使用率',
          type: 'gauge',
          max: 100,
          radius: '70%', // 半径
          startAngle: 215, // 起始位置
          endAngle: -35, // 终点位置
          detail: {
            formatter: '{value}%'
          },
          data: [{
            value: data.memoryUsage,
            name: ''
          }]
        }]
      }
      myChart2.setOption(option2)
    }
  }
}
</script>
<style lang="scss" scope>
  .container{
    overflow: hidden;
    p{
      font-size: 14px;color: #666;padding: 10px 0;
      .fl{
        float: left;
      }
      .fr{
        float: right;
      }
    }
    .loadAverage{
      p{
        text-align: center;
      }
      .title{
        font-size: 18px;font-weight: bold;color: #333;padding: 5px 0;margin: 0;
      }
      .number{
        font-size: 50px;color: #3d90d0
      }
    }
  }
</style>
