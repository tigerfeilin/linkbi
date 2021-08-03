<template>
  <div v-loading="loading" class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="图表名称" prop="searchVal">
        <el-input
          v-model="listQuery.name"
          clearable
          placeholder="请输入图表名称"
          style="width: 240px;"
          size="small"
          @keyup.enter.native="getList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="getList">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="addChart">新增图表</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="chartList" >
      <!--<el-table-column type="selection" width="55"></el-table-column>@selection-change="handleSelectionChange"-->
      <el-table-column label="序号" type="index" width="100" align="center">
        <template slot-scope="scope">
          <span>{{(listQuery.current - 1) * listQuery.size + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="图表名称"
        align="center"
        prop="name"
        :show-overflow-tooltip="true"
        width="300"
      />
      <el-table-column
        label="描述"
        align="center"
        prop="comment"
        :show-overflow-tooltip="true"
        width="500"
      />
      <!--<el-table-column
        label="权限"
        align="center"
        prop="tableStatus"
        :show-overflow-tooltip="true"
        width="100"
      />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="150" />-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="editChart(scope.row)"
            v-hasPermi="['metadata:table:edit']"
          >编辑</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="deleteChart(scope.row)"
            v-hasPermi="['metadata:table:delete']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.current"
      :limit.sync="listQuery.size"
      @pagination="getList"
    />
  </div>
</template>
<script>
    import { createChart, updateChart, getChartById, getChartList, deleteChart } from '@/api/dataview/chart'
    import { getDashboardList, addChart, listDashboardByChartId } from '@/api/dataview/dashboard'
    import * as dsQueryApi from '@/api/datasync/metadataquery'
    export default {
        data() {
            return {
                dashboardList: [],
                chartList: [],
                total:0,
                currentDashboard: undefined,
                editDialogVisible: false,
                dbObj: {},
                loading: false,
                isCollapse: false,
                listQuery:{
                    current: 1,
                    size: 10,
                    name:undefined,
                },
                rules: {
                    name: [{ required: true, message: 'this is required', trigger: 'change' }],
                    comment: [{ required: true, message: 'this is required', trigger: 'change' }]
                }
            }
        },
        created() {
            this.getList()
        },
        methods: {
            getList() {
                this.loading = true
                getChartList(this.listQuery).then(resp => {
                    this.loading = false
                    this.chartList = resp.data.rows
                    this.total = resp.data.total
                })
            },
            addChart() {
                this.$router.push('/dataview/editchart/create')
            },
            editChart(chart) {
                this.$router.push('/dataview/editchart/'+ chart.id)
            },
            deleteChart(chart) {
                const chartId = chart.id
                this.$confirm(this.$t('chart.deleteConfirm') +chart.name, this.$t('common.confirm')).then(() => {
                    deleteChart(chartId).then(() => {
                        this.getList()
                        this.msgSuccess(this.$t('common.deleteSuccess'))
                    })
                })
            }
        },
    }
</script>

<style lang="scss" scoped>
  .time {
    font-size: 12px;
    color: #999;
  }
  .boardText {
    font-size: 14px;
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 1;//控制行数
    -webkit-box-orient: vertical;
  }
  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .button {
    padding: 0;
    float: right;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }
</style>
