<template>
  <div class="app-container">
    <div class="build-container">
      <el-steps :active="active" finish-status="success">
        <el-step title="步骤 1" description="创建reader">1</el-step>
        <el-step title="步骤 2" description="创建writer">2</el-step>
        <el-step title="步骤 3" description="字段映射">3</el-step>
        <el-step title="步骤 4" description="创建">4</el-step>
      </el-steps>

      <div v-show="active===1" class="step1">
        <Reader ref="reader" v-bind:sourcedbmsg='sourcedbmsg'/>
      </div>
      <div v-show="active===2" class="step2">
        <Writer ref="writer" />
      </div>
      <div v-show="active===3" class="step3">
        <Mapper ref="mapper" />
      </div>
      <div v-show="active===4" class="step4">
        <el-form :rules="rules" :inline="true">
          <el-form-item label="任务名称：">
            <el-input v-model="jobName" placeholder="任务名称(默认取数据表名)"
                      style="width: 240px;"
                      size="small" />
          </el-form-item>
          <!--
          <el-form-item label="所属应用" prop="projectId">
            <el-select v-model="temp.projectId"
                       placeholder="请选择所属应用"
                       clearable
                       filterable
                       :filter-method="userFilter"
                       size="small"
                       style="width: 240px">
              <el-option v-for="item in jobProjectList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>-->
          <el-form-item>
            <el-button type="primary" @click="buildJson">1.创建json</el-button>
            <el-button type="primary" @click="handleJobTemplateSelectDrawer">{{ jobTemplate ? jobTemplate : "2.选择模板" }}</el-button>
            <el-button type="info" @click="handleCopy(inputData,$event)">复制json</el-button>
          </el-form-item>
        (步骤：创建json->选择模板->下一步)
        <el-drawer
          ref="jobTemplateSelectDrawer"
          title="选择模板"
          :visible.sync="jobTemplateSelectDrawer"
          direction="rtl"
          size="50%"
        >
          <el-table
            v-loading="listLoading"
            :data="list"
            element-loading-text="Loading"

            highlight-current-row
            destroy-on-close="true"
            @current-change="handleCurrentChange"
          >
            <el-table-column align="center" label="任务ID" width="80">
              <template slot-scope="scope">{{ scope.row.id }}</template>
            </el-table-column>
            <el-table-column label="任务描述" align="center" >
              <template slot-scope="scope">{{ scope.row.jobDesc }}</template>
            </el-table-column>
            <el-table-column label="所属应用" align="center" >
              <template slot-scope="scope">{{ scope.row.projectName }}</template>
            </el-table-column>
            <el-table-column label="Cron表达式" align="center">
              <template slot-scope="scope"><span>{{ scope.row.jobCron }}</span></template>
            </el-table-column>
            <el-table-column label="路由策略" align="center">
              <template slot-scope="scope"> {{ routeStrategies.find(t => t.value === scope.row.executorRouteStrategy).label }}</template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.current" :limit.sync="listQuery.size" @pagination="fetchData" />
        </el-drawer>


        </el-form>
        <div style="margin-bottom: 20px;" />
        <json-editor v-show="active===4" ref="jsonEditor" v-model="configJson" />
      </div>

      <el-button :disabled="active===1" style="margin-top: 12px;" @click="last">上一步</el-button>
      <el-button type="primary" style="margin-top: 12px;margin-bottom: 12px;" @click="next">下一步</el-button>
    </div>
  </div>
</template>

<script>
import * as dataxJsonApi from '@/api/datasync/dataxjson'
import * as jobTemplate from '@/api/datasync/jobtemplate'
import * as job from '@/api/datasync/jobinfo'
import * as jobProjectApi from '@/api/project/project'
import Pagination from '@/components/Pagination'
import JsonEditor from '@/components/JsonEditor'
import Reader from './reader'
import Writer from './writer'
import clip from '@/utils/clipboard'
import Mapper from './mapper'

export default {
  name: 'JsonBuild',
  components: { Reader, Writer, Pagination, JsonEditor, Mapper },
  data() {
    return {
      configJson: '',
      active: 1,
      jobTemplate: '',
      jobTemplateSelectDrawer: false,
      list: null,
      currentRow: null,
      listLoading: true,
      total: 0,
      jobName:'',
      inputData:'',
      listQuery: {
        current: 1,
        size: 10,
        jobGroup: 0,
        triggerStatus: -1,
        jobDesc: '',
        executorHandler: '',
        userId: 0
      },
      jobProjectList: [],
      allJobProjectList: [],
        sourcedbmsg:{
            sourceId:"",
            sourceSchema:"",
            sourceTableName:""
        },
      blockStrategies: [
        { value: 'SERIAL_EXECUTION', label: '单机串行' },
        { value: 'DISCARD_LATER', label: '丢弃后续调度' },
        { value: 'COVER_EARLY', label: '覆盖之前调度' }
      ],
      routeStrategies: [
        { value: 'FIRST', label: '第一个' },
        { value: 'LAST', label: '最后一个' },
        { value: 'ROUND', label: '轮询' },
        { value: 'RANDOM', label: '随机' },
        { value: 'CONSISTENT_HASH', label: '一致性HASH' },
        { value: 'LEAST_FREQUENTLY_USED', label: '最不经常使用' },
        { value: 'LEAST_RECENTLY_USED', label: '最近最久未使用' },
        { value: 'FAILOVER', label: '故障转移' },
        { value: 'BUSYOVER', label: '忙碌转移' }
        // { value: 'SHARDING_BROADCAST', label: '分片广播' }
      ],
      rules: {
        projectId: [{ required: false, message: 'projectId is required', trigger: 'change' }]
      },
      triggerNextTimes: '',
      registerNode: [],
      jobJson: '',
      temp: {
        id: undefined,
        jobGroup: '',
        jobCron: '',
        jobDesc: '',
        executorRouteStrategy: '',
        executorBlockStrategy: '',
        childJobId: '',
        executorFailRetryCount: '',
        alarmEmail: '',
        executorTimeout: '',
        userId: 0,
        jobConfigId: '',
        executorHandler: 'executorJobHandler',
        glueType: 'BEAN',
        jobJson: '',
        projectId: '',
        executorParam: '',
        replaceParam: '',
        jvmParam: '',
        incStartTime: ''
      }
    }
  },
  created() {
    // this.getJdbcDs()
    this.getJobProject()
  },
  methods: {
    next() {
      const fromColumnList = this.$refs.reader.getData().columns
      const toColumnsList = this.$refs.writer.getData().columns
      // const fromTableName = this.$refs.reader.getData().tableName
      // 第一步 reader 判断是否已选字段
      if (this.active === 1) {
        // 实现第一步骤读取的表和字段直接带到第二步骤
        // this.$refs.writer.sendTableNameAndColumns(fromTableName, fromColumnList)
        // 取子组件的数据
        // console.info(this.$refs.reader.getData())
          const readerData = this.$refs.reader.getData();
          this.$refs.writer.sendDBMsg(readerData.datasourceId, readerData.tableSchema,readerData.tableName)
        this.active++
      } else {
        // 将第一步和第二步得到的字段名字发送到第三步
        if (this.active === 2) {
          this.$refs.mapper.sendColumns(fromColumnList, toColumnsList)
        }
        if (this.active === 4) {
          this.temp.jobJson = this.configJson
          //this.temp.projectId = this.projectId
          if(this.temp.projectId === ''){
            this.msgWarn("请输入应用名称！");
            return
          }
            const jobData = this.temp
            this.$confirm('确定创建该任务?', "警告", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(function() {
                return job.createJob(jobData);
            }).then(() => {
                this.msgSuccess("创建成功");
                this.active = 1
            })
        } else {
          this.active++
        }
      }
    },
    last() {
      if (this.active > 1) {
        this.active--
      }
    },
    userFilter(query = '') {
      let arr = this.allJobProjectList.filter((item) => {
        return item.name.includes(query)
      })
      if (arr.length > 50) {
        this.jobProjectList = arr.slice(0, 50)
      } else {
        this.jobProjectList = arr
      }
    },
    getJobProject() {
      jobProjectApi.getJobProjectList().then(response => {
        this.allJobProjectList = response.data
        this.userFilter()
      })
    },
    // 创建json
    buildJson() {
      const readerData = this.$refs.reader.getData()
      const writeData = this.$refs.writer.getData()
      const readerColumns = this.$refs.mapper.getLColumns()
      const writerColumns = this.$refs.mapper.getRColumns()
      const hiveReader = {
        readerPath: readerData.path,
        readerDefaultFS: readerData.defaultFS,
        readerFileType: readerData.fileType,
        readerFieldDelimiter: readerData.fieldDelimiter,
        readerSkipHeader: readerData.skipHeader
      }
      const hiveWriter = {
        writerDefaultFS: writeData.defaultFS,
        writerFileType: writeData.fileType,
        writerPath: writeData.path,
        writerFileName: writeData.fileName,
        writeMode: writeData.writeMode,
        writeFieldDelimiter: writeData.fieldDelimiter
      }
      const hbaseReader = {
        readerMode: readerData.mode,
        readerMaxVersion: readerData.maxVersion,
        readerRange: readerData.range
      }
      const hbaseWriter = {
        writerMode: writeData.mode,
        writerRowkeyColumn: writeData.rowkeyColumn,
        writerVersionColumn: writeData.versionColumn,
        writeNullMode: writeData.nullMode
      }
      const mongoDBReader = {}
      const mongoDBWriter = {
        upsertInfo: writeData.upsertInfo
      }
      const rdbmsReader = {
        readerSplitPk: readerData.splitPk,
        whereParams: readerData.where,
        querySql: readerData.querySql
      }
      const rdbmsWriter = {
        preSql: writeData.preSql,
        postSql: writeData.postSql
      }
      const obj = {
        readerDatasourceId: readerData.datasourceId,
        readerTables: [readerData.tableName],
        readerColumns: readerColumns,
        writerDatasourceId: writeData.datasourceId,
        writerTables: [writeData.tableName],
        writerColumns: writerColumns,
        hiveReader: hiveReader,
        hiveWriter: hiveWriter,
        rdbmsReader: rdbmsReader,
        rdbmsWriter: rdbmsWriter,
        hbaseReader: hbaseReader,
        hbaseWriter: hbaseWriter,
        mongoDBReader: mongoDBReader,
        mongoDBWriter: mongoDBWriter
      }
      if (readerData.tableSchema !== '')
      {
          if(this.$refs.reader.dataSource === 'oracle')
              obj.readerTables = ['"' + readerData.tableSchema+ '"' + '.' + '"' + readerData.tableName+ '"'];
          else
              obj.readerTables = [readerData.tableSchema + '.' + readerData.tableName];

      }
      if (writeData.tableSchema !== '')
      {
          if(this.$refs.writer.dataSource === 'oracle')
              obj.writerTables = [ '"' + writeData.tableSchema+ '"' + '.' + '"' +writeData.tableName + '"'];
          else
              obj.writerTables = [writeData.tableSchema + '.' + writeData.tableName];
      }
      // 调apiconsole.log(writeData.tableName)
      dataxJsonApi.buildJobJson(obj).then(response => {
        this.configJson = JSON.parse(response.data)
      })
    },
    handleCopy(text, event) {
      clip(this.configJson, event);
      this.msgSuccess('复制成功');
    },
    handleJobTemplateSelectDrawer() {
      this.jobTemplateSelectDrawer = !this.jobTemplateSelectDrawer
      if (this.jobTemplateSelectDrawer) {
        this.fetchData()
        this.getExecutor()
      }
    },
    getReaderData() {
      return this.$refs.reader.getData()
    },
    getExecutor() {
      jobTemplate.getExecutorList().then(response => {
        const { content } = response
        this.executorList = content
      })
    },
    fetchData() {
      this.listLoading = true
      jobTemplate.getList(this.listQuery).then(response => {
        const { content } = response
        this.total = content.recordsTotal
        this.list = content.data
        this.listLoading = false
      })
    },
    handleCurrentChange(val) {
      let loc_projectId = this.temp.projectId
      this.temp = Object.assign({}, val)
      if(loc_projectId !== undefined && loc_projectId !== null && loc_projectId !== '')
        this.temp.projectId = loc_projectId
      this.temp.id = undefined
      this.temp.jobDesc = this.jobName == '' ? this.getReaderData().tableName : this.jobName;
      this.$refs.jobTemplateSelectDrawer.closeDrawer()
      this.jobTemplate = val.id + '(' + val.jobDesc + ')'
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
