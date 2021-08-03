<template>
  <div>
    <!--<el-card body-style="padding:0;" style="margin-bottom: 20px;" class="panel-header">

      <div slot="header" style="display: flex; justify-content:space-between;">
        <span>
          <span class="back-button" @click="this.$router.go(-1)">
            <i class="el-icon-back" />
            <span>{{ $t('common.back') }}</span>
          </span>
          <span v-if="this.$route.params.id !== 'create'">{{ $t('chart.createNewChart') }}</span>
          <span v-else>{{ $t('chart.editChart') }}</span>
          <el-button size="mini" type="text" style="margin-left:10px;" @click="viewAllChart">
            {{ $t('chart.allCharts') }}
          </el-button>
        </span>
        <span>
          <el-button size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-download" @click="handleDownload" />
          <el-button v-if="this.$route.params.id !== 'create'" size="mini" type="primary" style="float: right;margin:0 10px 0 0;" @click="handleLinkDB">{{ $t('chart.addToDashboard') }}</el-button>
          <el-button size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-save" @click="handleSave">{{ $t('common.save') }} </el-button>
          <el-button v-if="this.$route.params.id !== 'create'" size="mini" type="primary" style="float: right;margin:0 10px 0 0;" @click="$router.replace(`/chartpanel/create`)">{{ $t('chart.createNewChart') }}</el-button>
        </span>
      </div>
    </el-card>-->

    <div v-loading="loading" class="app-container" style="display: flex;">
      <el-card id="dataPanel" style="width:300px;margin-right: 20px;text-align:center;">
        <data-panel ref="dataPanel" :result-loading="loading" :data-src="dataSrc" @change="handleDataSrcChange" />
      </el-card>

      <el-card style="width: 100%;" body-style="padding: 10px 20px;">
        <div class="form-wrapper">
          <el-form id="formPanel" size="mini" class="analysis-form">
            <el-form-item id="dimensionInput" :label="$t('chart.dimensions')">
              <draggable v-model="dimensions" :group="{name: 'col',pull: true, put: true}" class="draggable-wrapper" @change="handleDimensionChange">
                <el-tag v-for="col in dimensions" :key="col.Column" class="draggable-item" size="small" closable @close="handleCloseDimensionTag(col)">
                  {{ col.Column }}
                </el-tag>
              </draggable>
            </el-form-item>

            <el-form-item id="fieldInput" :label="$t('chart.values')">
              <draggable v-model="caculCols" :group="{name: 'col',pull: true, put: true}" class="draggable-wrapper" @change="handleColChange">
                <el-tag v-for="col in caculCols" :key="col.Column" size="small" closable class="selected-field" @close="handleCloseColTag(col)">
                  <el-select v-model="col.calculFunc" class="draggable-item" size="mini" closable style="background: rgba(0,0,0,0);">
                    <el-option v-for="(item, optIndex) in col.availableFunc" :key="optIndex" :label="`${col.Column}(${item.name})`" :value="item.func" />
                  </el-select>
                </el-tag>
              </draggable>
            </el-form-item>

            <orderPanel v-model="orderByStrs" />

            <filterPanel :filters.sync="currentFilters" :disabled="!allSelected || allSelected.length===0" @change="handleAddFilter" />

            <el-form-item>
              <div class="limit-input">
                <span v-show="!editLimit">
                  {{ $t('chart.limit', [limit]) }}
                  <el-button type="text" @click="editLimit=true">{{ $t('common.edit') }}</el-button>
                </span>
                <span v-show="editLimit">
                  <el-input-number v-model="limit" :disabled="loading" size="mini" style="width:100px;" @blur="editLimit=false" />
                  <el-button size="mini" @click="editLimit=false">{{ $t('common.confirm') }}</el-button>
                </span>
              </div>
            </el-form-item>
          </el-form>
          <el-form class="chart-form" size="mini" label-position="top">
            <el-form-item :label="$t('chart.chartName')+':'">
              <el-input v-model="chartName" size="mini" :placeholder="$t('chart.namePlaceholder')" />
            </el-form-item>
            <el-form-item :label="$t('chart.chartDesc')+':'">
              <el-input v-model="chartDesc" size="mini" :placeholder="$t('chart.descPlaceholder')" />
            </el-form-item>
            <el-form-item>
              <el-tooltip class="item" effect="dark" :content="$t('chart.download')" placement="top-start">
                <el-button size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-download" @click="handleDownload" circle/>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" :content="$t('chart.save')" placement="top-start">
                <el-button size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-folder-checked" @click="handleSave" circle/>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" :content="$t('chart.addToDashboard')" placement="top-start">
                <el-button :disabled="this.$route.params.id === 'create'" size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-folder-add" @click="handleLinkDB" circle></el-button>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" :content="$t('chart.createNewChart')" placement="top-start">
                <el-button :disabled="this.$route.params.id === 'create'" size="mini" type="primary" style="float: right;margin:0 10px 0 0;" icon="el-icon-circle-plus-outline" @click="$router.replace(`/dataview/editchart/create`)" circle/>
              </el-tooltip>
            </el-form-item>
          </el-form>
        </div>
        <visualize-panel id="vizPanel" v-loading="loading" :data="result" :chart-type.sync="chartType" :schema="allSelected" />
      </el-card>
    </div>
    <el-dialog :title="$t('chart.myChart')" :visible.sync="showMyCharts">
      <el-table :data="myChartList">
        <el-table-column :label="$t('chart.chartName')" width="200" prop="chart_name" />
        <el-table-column :label="$t('chart.chartDesc')" prop="desc" />
        <el-table-column :label="$t('common.operation')" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" icon="el-icon-edit" @click="switchChart(scope.row)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteChart(scope.row)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="showMyCharts = false">{{ $t('common.close') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="$t('dashboard.dashboardList')" :visible.sync="showDashboards">
      <div style="text-align:center;">
        <el-select v-model="selectedDb" size="small">
          <el-option v-for="item in dashboardList" :key="item.id" :label="item.name" :disabled="isDbDisbaled(item)" :value="item.id" />
        </el-select>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="showDashboards = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" size="small" @click="linkDb">{{ $t('common.confirm') }}</el-button>
      </span>
    </el-dialog>
    <!-- <el-tooltip content="帮助中心" placement="top"> -->
    <el-dropdown class="help-center-wrapper" placement="top" size="mini" @command="handleHelp">
      <div class="help-center">
        <i class="el-icon-question" />
      </div>
      <el-dropdown-menu slot="dropdown" size="mini">
        <el-dropdown-item command="guide">
          {{ this.$t('common.openGuide') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <!-- </el-tooltip> -->
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import Driver from 'driver.js' // import driver.js
import 'driver.js/dist/driver.min.css' // import driver.js css

import filterPanel from './components/filterPanel'
import orderPanel from './components/orderPanel'
import visualizePanel from './components/visualizePanel'
import dataPanel from './components/dataPanel'

import { createChart, updateChart, getChartById, chartList, deleteChart } from '@/api/dataview/chart'
import { getDashboardList, addChart, listDashboardByChartId } from '@/api/dataview/dashboard'
import * as dsQueryApi from '@/api/datasync/metadataquery'
import { buildSqlSentence,buildSql_Filters } from '@/utils/buildSentence'
import * as datasourceApi from '@/api/datasource/datasource'
import steps from './guideSteps'

const driver = new Driver()

export default {
  name: 'ChartPanel',
  components: { visualizePanel, dataPanel, draggable, filterPanel, orderPanel },
  data() {
    return {
      loading: false,
      result: [],
      dataSrc: {},
      limit: 200,
      orderByStrs: [],
      filterStr: undefined,
      editLimit: false,
      currentFilters: [],
      chartType: 'table',
      chartName: undefined,
      chartDesc: undefined,
      showMyCharts: false,
      myChartList: [],
      showDashboards: false,
      dashboardList: [],
      selectedDb: undefined,
      linkedDbIds: [],
      execInstance: null,
      dbList:[],
      listQuery:{
          current:1,
          size:10000
      }
    }
  },
  computed: {
    caculCols: {
      get() {
        return this.$store.state.chart.caculCols
      }, set(value) {
        this.$store.commit('chart/SET_CACUL_COLS', value)
      }
    },
    dimensions: {
      get() {
        return this.$store.state.chart.dimensions
      },
      set(value) {
        this.$store.commit('chart/SET_DIMENSIONS', value)
      }
    },
    allSelected() {
      return this.dimensions.concat(this.caculCols)
    },
    sqlSentence() {
        return buildSqlSentence({
        dbType: this.dataSrc.db_type,
        dataSrc: this.dataSrc.query_sql,
        selectedCalcul: this.caculCols,
        selectedDimension: this.dimensions,
        orderByStrs: this.orderByStrs,
        filterStr: this.filterStr,
        limit: this.limit
      })
    }
  },
  watch: {
    sqlSentence(value) {
      if (value) {
        this.fetchData(value)
      } else {
        this.result = []
      }
    },
    '$route.params.id': {
      immediate: true,
      handler() {
        if (this.$route.params.id !== 'create') {
          getChartById(this.$route.params.id).then(resp => {
            const chart = resp.data
            this.chartName = chart.name
            this.chartDesc = chart.comment
            const content = JSON.parse(chart.content) || {}
            this.dataSrc.mode_id = chart.modeId
            this.dataSrc.query_sql = content.query_sql
            this.dataSrc.source_id = content.source_id
            this.chartType = content.chartType
            this.limit = content.limit || 200
            this.currentFilters = content.filters
            this.orderByStrs = content.orderByStrs
              if(this.dbList === undefined || this.dbList.length === 0) {
                  datasourceApi.list(this.listQuery).then(response => {
                      this.dbList = response.data.records
                      this.dbList.find((item) => {
                          if (item.id === this.dataSrc.source_id) {
                              this.dataSrc.db_type = item.datasource
                          }
                      })
                      this.$store.commit('chart/SET_CACUL_COLS', content.selectedCalcul)
                      this.$store.commit('chart/SET_DIMENSIONS', content.selectedDimension)
                      this.$refs.dataPanel.initWithDataSrc(this.dataSrc)
                  }).catch(e=>{
                      this.msgError(e)
                  })
              }
              else{
                  this.dbList.find((item) => {
                      if (item.id === this.dataSrc.source_id) {
                          this.dataSrc.db_type = item.datasource
                      }
                  })
                  this.$store.commit('chart/SET_CACUL_COLS', content.selectedCalcul)
                  this.$store.commit('chart/SET_DIMENSIONS', content.selectedDimension)
                  this.$refs.dataPanel.initWithDataSrc(this.dataSrc)
              }

          })
        } else {
          this.chartName = undefined
          this.chartDesc = undefined
          this.$store.commit('chart/SET_CACUL_COLS', [])
          this.$store.commit('chart/SET_DIMENSIONS', [])
          this.$nextTick(() => {
            this.$refs.dataPanel.initWithDataSrc()
          })
        }
      }
    }
  },
  methods: {
    fetchData(sqlSentence) {
      this.loading = true
        dsQueryApi.adhoc_nolimit({datasourceId:this.dataSrc.source_id,querySql:sqlSentence}).then(response => {
            //console.log(response);
            this.loading = false
            this.result = response.rows
            console.log(this.result)
        }).catch((e) =>
        {
            this.loading = false
            this.msgError(e)
        })
    },

    handleDataSrcChange(value) {
      this.dataSrc = value
        if(this.dbList === undefined || this.dbList.length === 0) {
            datasourceApi.list(this.listQuery).then(response => {
                this.dbList = response.data.records
                this.dbList.find((item) => {
                    if (item.id === this.dataSrc.source_id) {
                        this.dataSrc.db_type = item.datasource
                    }
                })
            }).catch(e=>{
                this.msgError(e)
            })
        }
        else{
            this.dbList.find((item) => {
                if (item.id === this.dataSrc.source_id) {
                    this.dataSrc.db_type = item.datasource
                }
            })
        }
      this.$store.commit('chart/SET_CACUL_COLS', [])
      this.$store.commit('chart/SET_DIMENSIONS', [])
      this.filterStr = undefined
      this.orderByStrs = []
    },
    handleColChange(evt) {
      if (evt.added) {
        this.$store.commit('chart/ADD_CACUL_COL', evt.added.element)
      }
    },
    handleDimensionChange(evt) {
      if (evt.added) {
        this.$store.commit('chart/ADD_DIMENSION_COL', evt.added.element)
      }
    },
    handleCloseColTag(col) {
      this.$store.commit('chart/DELETE_CACUL_COL', col)
    },
    handleCloseDimensionTag(col) {
      this.$store.commit('chart/DELETE_DIMENSION_COL', col)
    },
    handleAddFilter(value) {
      if(value)
        this.filterStr =  buildSql_Filters({dbType:this.dataSrc.db_type,filters:value}).join(' and ')
    },
    handleSave() {
      if (!this.chartName) {
        this.$message({
          type: 'warning',
          message: this.$t('chart.chartNameWarning')
        })
        return
      }
      const chartId = this.$route.params.id === 'create' ? undefined : this.$route.params.id
      const obj = {
        query_sql: this.dataSrc.query_sql,
        source_id: this.dataSrc.source_id,
        orderByStrs: this.orderByStrs,
        limit: this.limit,
        selectedCalcul: this.caculCols,
        selectedDimension: this.dimensions,
        chartType: this.chartType,
        filters: this.currentFilters
      }
      const data = {
        id: chartId,
        name: this.chartName,
        modeId: this.dataSrc.mode_id,
        comment: this.chartDesc,
        content: JSON.stringify(obj)
      }
      if (chartId) {
        updateChart(data).then(resp => {
          this.$message({
            type: 'success',
            message: this.$t('common.saveSuccess')
          })
        })
      } else {
        createChart(data).then(resp => {
          this.$router.replace('/dataview/editchart/' + resp.data)
          this.$message({
            type: 'success',
            message: this.$t('common.saveSuccess')
          })
        })
      }
    },
    handleLinkDB() {
      this.showDashboards = true
      this.getDbByChart(this.$route.params.id)
      getDashboardList(this.listQuery).then(resp => {
        this.dashboardList = resp.data.rows
      })
    },
    getDbByChart(id) {
        listDashboardByChartId(id).then(resp => {
        this.linkedDbIds = resp.data || []
      })
    },
    isDbDisbaled(db) {
      return !!this.linkedDbIds.find(id => id === db.id)
    },
    linkDb() {
      const data = {
          chartId: this.$route.params.id,
          dashboardId: this.selectedDb
      }
      this.showDashboards = false
      addChart(data).then(resp => {
        this.getDbByChart(this.$route.params.id)
        this.$message({
          type: 'success',
          message: this.$t('common.saveSuccess')
        })
      })
    },
    viewAllChart() {
      this.showMyCharts = true
      chartList().then(resp => {
        this.myChartList = resp.data
      })
    },
    switchChart(chart) {
      this.$confirm(this.$t('chart.beforeLeaveConfirm'), this.$t('common.confirm')).then(() => {
        this.$router.replace(`/dataview/editchart/${chart.chart_id}`)
        this.showMyCharts = false
      })
    },
    deleteChart(chart) {
      this.$confirm(this.$t('chart.deleteConfirm', chart.chart_name), this.$t('common.confirm')).then(() => {
        deleteChart(chart.id).then(() => {
          if (this.$route.params.id === chart.id) {
            this.$router.push('/dataview/editchart/create')
            this.showMyCharts = false
          } else {
            this.viewAllChart()
          }
          this.$message({
            type: 'success',
            message: this.$t('common.deleteSuccess')
          })
        })
      })
    },
    handleHelp(command) {
      if (command === 'guide') {
        driver.defineSteps(steps)
        driver.start()
      }
    },
    handleDownload() {
      import('@/utils/Export2Excel').then(excel => {
        const tHeader = this.allSelected.map(item => item.Column)
        const filterVal = tHeader
        const data = this.formatJson(filterVal, this.result)
        excel.export_json_to_excel({ header: tHeader, data, filename: 'Table' + this.parseTime(Date.now(), '{m}{d}{h}{i}{s}'), autoWidth: true })
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v =>
        filterVal.map(j => {
          const tempArr = j.split('.')
          if (tempArr.length <= 1) {
            return v[j]
          } else {
            return tempArr.reduce(
              (pre, cur) => (pre[cur] ? pre[cur] : '--'),
              v
            )
          }
        })
      )
    }
  }
}
</script>
<style lang="scss" scoped>
.back-button {
  display: inline-block;
  padding-right: 10px;
  margin-right: 10px;
  border-right: 1px solid #909090;
  cursor: pointer;
  span {
    padding: 5px;
    font-size: 14px;
  }
}
.analysis-form {
  width: 100%;
  padding-right: 20px;
  ::v-deep .el-form-item--mini.el-form-item {
    margin-bottom: 10px;
  }
  ::v-deep .el-form-item--mini .el-form-item__label,.limit-input {
    color: #909399;
    font-size: 14px;
  }
}
.form-wrapper {
  display: flex;
}
.chart-form {
  width: 250px;
  ::v-deep .el-form-item--mini.el-form-item {
    margin-bottom: 10px;
  }
}
.draggable-wrapper {
  font-size: 14px;
  min-height: 30px;
  border-bottom: 1px solid #E4E7ED;
  .draggable-item {
    margin-right: 10px;
  }
  .el-select--mini {
      margin: 0;
    }
}
.selected-field {
  ::v-deep .el-input__inner {
    height: 10px;
    line-height: 20px;
    border: none;
    background-color: rgba($color: #fff, $alpha: 0);
    padding: 0;
  }
  ::v-deep .el-input__suffix {
      right: 0px;
    .el-input__suffix-inner {
      display: inline-block;
      height: 20px;
      line-height: 20px;
      .el-input__icon {
        font-size: 12px;
        line-height: 20px;
      }
    }
  }
}
.help-center-wrapper {
  cursor: pointer;
  position: fixed;
  bottom: 25px;
  right: 25px;
  .help-center {
    width: 45px;
    height: 45px;
    background: #fff;
    border-radius: 50%;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    line-height: 45px;
    font-size: 20px;
    color: #388dff;
    text-align: center;
    .el-dropdown {
      font-size: 20px;
      color: #239aff;
    }

  }
}

</style>
