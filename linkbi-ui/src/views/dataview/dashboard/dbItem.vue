<template>
  <div class="dashboard-wrapper">
    <div class="tool-bar">
      <div v-show="!dbDescVisible">
        <span class="db-name">{{ dashboard.name }}</span>
        <span>{{ dashboard.comment }}</span>
        <el-button v-show="mode === 'edit'" type="text" size="mini" @click="dbDescVisible=true">
          {{ $t('common.edit') }}
        </el-button>
      </div>
      <el-form v-show="dbDescVisible && mode === 'edit'" :inline="true" style="">
        <el-form-item>
          <el-input size="mini" v-model="dashboard.name" style="width: 300px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input size="mini" v-model="dashboard.comment" style="width: 300px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button v-show="mode === 'edit'" type="primary" size="mini" @click="updateDbDesc">
          {{ $t('common.confirm') }}
          </el-button>
        </el-form-item>
      </el-form>
      <div v-show="mode === 'edit'">
        <el-button type="primary" size="mini" @click="handleShare">
          {{ $t('common.share') }}
        </el-button>
        <el-button type="primary" size="mini" @click="handleLinkChart">
          {{ $t('dashboard.addChart') }}
        </el-button>
      </div>
    </div>
    <grid-layout
      v-if="charts.length!==0"
      v-loading="loading"
      :layout="layout || []"
      :col-num="24"
      :row-height="30"
      :is-draggable="mode === 'edit'"
      :is-resizable="mode === 'edit'"
      :is-mirrored="false"
      :vertical-compact="true"
      :pane-container="false"
      :margin="[10, 10]"
      :use-css-transforms="true"
      style="min-height: 500px;"
      @layout-updated="handleLayoutChange"
    >
      <grid-item
        v-for="item in layout || []"
        :key="item.index"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.i"
        @resized="handleResize"
      >
        <el-card v-loading="chartLoading[item.i]" class="visualize-card" body-style="padding: 10px;">
          <div slot="header" class="operation-bar">
            <div>
              <span>{{ getChartItem(item.i).name }}</span>
            </div>
            <div>
              <i v-show="mode === 'edit'" class="el-icon-edit" @click="handleEdit(getChartItem(item.i))" />
              <i v-show="mode === 'edit'" class="el-icon-delete" @click="handleDelete(getChartItem(item.i))" />
              <el-tooltip :content="getChartItem(item.i).comment" class="item" effect="dark" placement="top-end">
                <i class="el-icon-info" style="color:#409eff;cursor:pointer;" />
              </el-tooltip>
            </div>
          </div>
          <visualize-panel :key="item.index" :ref="`chartInstance${item.i}`" :data="results[item.i]" :schema="getChartItem(item.i).content.allSelected" :chart-type.sync="getChartItem(item.i).content.chartType" :is-edit-mode="false" :chart-style="{height: `${item.h*30 + 10 * (item.h-1) - 60}px`}" />
        </el-card>
      </grid-item>
    </grid-layout>
    <div v-if="charts.length === 0 && mode === 'edit'" v-loading="loading" class="welcome-container">
      <el-button type="primary" size="mini" @click="handleLinkChart">
        {{ $t('dashboard.addChart') }}
      </el-button>
      <div>
        <el-link type="info" :underline="false">
          <router-link to="/chartpanel/create">
            {{ $t('dashboard.emptyDashboardTip') }}
          </router-link>
        </el-link>
      </div>
    </div>
    <el-dialog :title="$t('chart.myChart')" :visible.sync="showChartList" width="40%" >
      <el-row>
        <el-form>
          <el-form-item>
            <el-button type="primary" size="mini" @click="$router.push('/dataview/editchart/create')">
              {{ $t('chart.createNewChart') }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-table :data="myChartList">
        <el-table-column :label="$t('common.name')" width="200" prop="name" />
        <el-table-column :label="$t('common.desc')" prop="comment" />
        <el-table-column :label="$t('common.operation')" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" :disabled="isExisted(scope.row)" @click="linkChart(scope.row)">
              {{ $t('common.add') }}
            </el-button>
            <el-button size="mini" type="warning" @click="$router.push(`/dataview/editchart/${scope.row.id}`)">
              {{ $t('common.edit') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="showChartList = false">{{ $t('common.close') }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { GridLayout } from 'vue-grid-layout'
import { GridItem } from 'vue-grid-layout'
import visualizePanel from '../chart/components/visualizePanel'
import * as dsQueryApi from '@/api/datasync/metadataquery'
import { listChartByDashboardId, getDashboardById,updateDashboard, addChart, deleteChart } from '@/api/dataview/dashboard'
import { getChartList } from '@/api/dataview/chart'
import { buildSqlSentence,buildSql_Filters } from '@/utils/buildSentence'
import * as datasourceApi from '@/api/datasource/datasource'
function isLineOverLap(line1, line2) {
  const start1 = {
    x: line1[0][0],
    y: line1[0][1]
  }
  const end1 = {
    x: line1[1][0],
    y: line1[1][1]
  }
  const start2 = {
    x: line2[0][0],
    y: line2[0][1]
  }
  const end2 = {
    x: line2[1][0],
    y: line2[1][1]
  }
  if (start1.y === start2.y && end1.y === end2.y) {
    if (start1.x >= start2.x && start1.x <= end2.x) {
      return true
    } else {
      return false
    }
  } else {
    return false
  }
}
export default {
  components: { GridLayout, GridItem, visualizePanel },
    props: {
        dashboard: {
            required: false,
            type: Object,
            default: () => {
                return {}
            }
        },
        mode: {
            required: false,
            type: String,
            default: 'view'
        }
    },
  data() {
    return {
        dbDescVisible:false,
        charts: [],
        results: {},
        loading: false,
        layout: [],
        content:undefined,
        myChartList: [],
        dbList:[],
        showChartList: false,
        chartLoading: {},
        listQuery:{
            current : 1,
            size : 10000
        }
    }
  },
  watch: {
    'dashboard.id': {
      immediate: true,
      handler(value) {
        if (!value) return
        this.getList(value)
      }
    }
  },
    created() {
        this.getDBList()
    },
  methods: {
      getDBList()
      {
          this.loading = true
          datasourceApi.list(this.listQuery).then(response => {
              this.dbList = response.data.records
              this.loading = false
          })
      },
    getList() {
      this.charts = []
      this.layout = []
      this.loading = true
        const dashboardId = this.dashboard.id
        listChartByDashboardId(dashboardId).then(resp => {
        this.loading = false
        this.charts = resp.data || []
        let filterStrs = []
        this.content = JSON.parse(this.dashboard.content)
        const layout = (this.content && this.content.layout) || []
        this.charts.forEach((chart, index) => {
          this.$set(this.results, chart.id, [])
          this.$set(this.chartLoading, chart.id, false)
          chart.content = JSON.parse(chart.content)
          chart.content.allSelected = []
          chart.content.allSelected = chart.content.allSelected.concat(chart.content.selectedDimension).concat(chart.content.selectedCalcul)
          let datasourceType = undefined
          this.dbList.find((item) => {
               if (item.id === chart.content.source_id) {
                   datasourceType = item.datasource
               }
          })
          if (chart.content.filters) {
            filterStrs = buildSql_Filters({dbType:datasourceType,filters:chart.content.filters}) //chart.content.filters.map(buildFilterSentence)
          }
          const sqlSentence = buildSqlSentence({
            dbType:datasourceType,
            dataSrc: chart.content.query_sql,
            selectedCalcul: chart.content.selectedCalcul,
            selectedDimension: chart.content.selectedDimension,
            orderByStrs: chart.content.orderByStrs,
            filterStr: filterStrs.join(' and '),
            limit: chart.content.limit
          })
          this.exeSql(sqlSentence, chart, index)
          if (!layout.find(layoutItem => layoutItem.id === chart.id)) {
            this.generatePosition(chart, layout, index)
          }
        })
        this.layout = layout.filter(item => {
          return this.charts.find(chart => chart.id === item.id)
        })
        this.handleLayoutChange()
      })
    },
    updateDbDesc()
    {
        if (this.mode === 'view') return
        updateDashboard(this.dashboard)
        this.dbDescVisible = false
    },
    getChartItem(id) {
      return this.charts.find(chart => chart.id === id)
    },
    handleCaculPos(layout) {
      // const layout = JSON.parse(JSON.stringify(layout))
      const bottomItems = []
      layout.forEach(i => {
        i.yy = i.y + i.h
        i.xx = i.x + i.w
        i.bottomLine = [[i.x, i.yy], [i.xx, i.yy]]
        i.topLine = [[i.x, i.y], [i.xx, i.y]]
      })
      layout.forEach(i => {
        const flag = layout.every(j => {
          return !isLineOverLap(i.bottomLine, j.topLine)
        })
        if (flag) {
          bottomItems.push(i)
        }
      })
      return bottomItems
    },
    generatePosition(chart, layout, index) {
      let posObj
      if (layout.length === 0) {
        posObj = {
          id: chart.id,
          x: 0,
          y: 0,
          w: 12,
          h: 9,
          i: chart.id
        }
      } else {
        const bottomItems = this.handleCaculPos(layout)
        const highestItem = bottomItems.reduce((result, item) => {
          if (result.bottomLine[0][1] > item.bottomLine[0][1]) {
            result = item
          }
          return result
        }, bottomItems[0])
        posObj = {
          id: chart.id,
          x: highestItem.x,
          y: highestItem.yy,
          w: highestItem.w,
          h: 9,
          i: chart.id
        }
      }
      layout.push(posObj)
    },
    handleShare() {
      const h = this.$createElement
      const link = 'http://' + location.host + '/dashboard/' + this.dashboard.id
      //const link = 'http://${location.host}/fullscreendb/${this.dashboard.id}'
      this.$msgbox({
        title: this.$t('dashboard.shareLink'),
        message: h('p', null, [
          h('a', { style: 'color: #205cd8', attrs: { href: link, target: '_blank' }}, link)
        ])
      })
    },
    handleLinkChart() {
        getChartList(this.listQuery).then(resp => {
        this.myChartList = resp.data.rows
        this.showChartList = true
      })
    },
    linkChart(chart) {
      const data = {
        chartId: chart.id,
        dashboardId: this.dashboard.id
      }
      addChart(data).then(resp => {
        this.showChartList = false
        this.getList()
        this.$message({
          type: 'success',
          message: this.$t('common.saveSuccess')
        })
      })
    },
    isExisted(chart) {
      return this.charts.findIndex(item => item.id === chart.id) >= 0
    },
    handleEdit(chart) {
      this.$router.push(`/dataview/editchart/${chart.id}`)
    },
    handleDelete(chart) {
      this.$confirm(this.$t('dashboard.removeChartConfirm'), this.$t('common.confirm'), {
        type: 'warning'
      }).then(() => {
        // deleteChart(index)
        const deleteChartIndex = this.layout.findIndex(item => item.id === chart.id)
        const layout = JSON.parse(JSON.stringify(this.layout))
        layout.splice(deleteChartIndex, 1)
        this.content.layout = layout
        this.dashboard.content = JSON.stringify(this.content)
        const data = {
          chartId: chart.id,
          dashboardId: this.dashboard.id
        }
        //const obj = Object.assign({}, this.dashboard)
        Promise.all([updateDashboard(this.dashboard), deleteChart(data)]).then(resp => {
          this.getList()
          this.$message({
            type: 'success',
            message: this.$t('common.deleteSuccess')
          })
        })
      })
    },
    handleLayoutChange() {
      if (this.mode === 'view') return
      this.dashboard.content = this.dashboard.content || {}
      this.content.layout = this.layout
      this.dashboard.content = JSON.stringify(this.content)
      updateDashboard(this.dashboard)
      //this.dashboard.content = JSON.parse(this.dashboard.content)
    },
    handleResize(i, newH, newW, newHPx, newWPx) {
        console.log(newH + "-" + newW + "-" + newHPx + "-" + newWPx);
      this.$refs[`chartInstance${i}`][0].$children[0].$emit('resized')
    },
    exeSql(sqlSentence, item, index) {
      this.$set(this.chartLoading, item.id, true)
      if (!sqlSentence) {
        this.$message.warning(this.$t('dashboard.chartQueryException', item.chart_name))
        this.$set(this.chartLoading, item.id, false)
        return
      }
        dsQueryApi.adhoc_nolimit({datasourceId:item.content.source_id,querySql:sqlSentence}).then(response => {
            //console.log(response);
            this.$set(this.chartLoading, item.id, false)
            this.$set(this.results, item.id, response.rows)
        }).catch((e) => {
            this.$set(this.chartLoading, item.id, false)
            this.msgWarn(e)
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.tool-bar {
  display: flex;
  justify-content: space-between;
  border-top: none;
  height: 45px;
  line-height: 45px;
  color:#303133;
  padding: 0 10px;
  position: relative;
  .db-name {
    font-size: 1.2em;
    font-weight: 600;
    color: #909399;
    margin-left: 0;
  }
  span {
    color: #C0C4CC;
    font-size: 0.8em;
    margin-left: 10px;
  }
}
///deep/
.visualize-card {
  ::v-deep .el-card__header {
    padding: 0;
    .operation-bar {
      font-size: 14px;
      display: flex;
      justify-content: space-between;
      height: 35px;
      padding: 0 10px;
      line-height: 35px;
      z-index: 9;
      i {
        margin-right: 10px;
        color: #409EFF;
        cursor: pointer;
      }
    }
  }
}
.welcome-container {
  text-align: center;
  height: 500px;
  color:#C0C4CC;
  ::v-deep .el-button {
    margin-top: 200px;
    margin-bottom: 25px;
  }
}
</style>
