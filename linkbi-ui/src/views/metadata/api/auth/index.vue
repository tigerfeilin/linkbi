<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="所属应用" prop="projectId">
        <el-select v-model="listQuery.projectId"
                   placeholder="请选择所属应用"
                   clearable
                   size="small"
                   style="width: 240px">
          <el-option v-for="item in jobProjectList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="fetchData">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCreate">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"

      highlight-current-row
      style="width: 100%"
      size="medium"
    >
      <el-table-column align="center" label="appKey" width="300">
        <template slot-scope="scope">{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column label="所属应用" align="center" >
        <template slot-scope="scope">{{ scope.row.projectName }}</template>
      </el-table-column>
      <el-table-column label="到期时间" align="center" >
        <template slot-scope="scope">{{ scope.row.projectName }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="150">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.triggerStatus"
            active-color="#00A854"
            active-text="启用"
            :active-value="1"
            inactive-color="#F04134"
            inactive-text="冻结"
            :inactive-value="0"
            @change="changeSwitch(scope.row)"
          />
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleEditTable(scope.row)"
            v-hasPermi="['tool:gen:edit']"
          >编辑</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['tool:gen:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.current" :limit.sync="listQuery.size" @pagination="fetchData" />

  </div>
</template>

<script>
import * as executor from '@/api/executor/executor'
import * as job from '@/api/datasync/jobinfo'
import Cron from '@/components/Cron'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import JsonEditor from '@/components/JsonEditor'
import ShellEditor from '@/components/ShellEditor'
import PythonEditor from '@/components/PythonEditor'
import PowershellEditor from '@/components/PowershellEditor'
import * as datasourceApi from '@/api/datasource/datasource'
import * as jobProjectApi from '@/api/project/project'
import { isJSON } from '@/utils/validate'

export default {
  name: 'JobInfo',
  components: { Pagination, JsonEditor, ShellEditor, PythonEditor, PowershellEditor, Cron },
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
    const validateIncParam = (rule, value, callback) => {
      if (!value) {
        callback(new Error('Increment parameters is required'))
      }
      callback()
    }
    const validatePartitionParam = (rule, value, callback) => {
      if (!this.partitionField) {
        callback(new Error('Partition parameters is required'))
      }
      callback()
    }
    return {
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        jobGroup: 0,
        projectId: '',
        triggerStatus: -1,
        jobDesc: '',
        glueType: ''
      },
      showCronBox: false,
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        jobGroup: [{ required: true, message: 'jobGroup is required', trigger: 'change' }],
        executorRouteStrategy: [{ required: true, message: 'executorRouteStrategy is required', trigger: 'change' }],
        executorBlockStrategy: [{ required: true, message: 'executorBlockStrategy is required', trigger: 'change' }],
        glueType: [{ required: true, message: 'jobType is required', trigger: 'change' }],
        projectId: [{ required: true, message: 'projectId is required', trigger: 'change' }],
        jobDesc: [{ required: true, message: 'jobDesc is required', trigger: 'blur' }],
        jobProject: [{ required: true, message: 'jobProject is required', trigger: 'blur' }],
        jobCron: [{ required: true, message: 'jobCron is required', trigger: 'blur' }],
        incStartId: [{ trigger: 'blur', validator: validateIncParam }],
        replaceParam: [{ trigger: 'blur', validator: validateIncParam }],
        primaryKey: [{ trigger: 'blur', validator: validateIncParam }],
        incStartTime: [{ trigger: 'change', validator: validateIncParam }],
        replaceParamType: [{ trigger: 'change', validator: validateIncParam }],
        partitionField: [{ trigger: 'blur', validator: validatePartitionParam }],
        datasourceId: [{ trigger: 'change', validator: validateIncParam }],
        readerTable: [{ trigger: 'blur', validator: validateIncParam }]
      },
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
        executorHandler: '',
        glueType: '',
        glueSource: '',
        jobJson: '',
        executorParam: '',
        replaceParam: '',
        replaceParamType: 'Timestamp',
        jvmParam: '',
        incStartTime: '',
        partitionInfo: '',
        incrementType: 0,
        incStartId: '',
        primaryKey: '',
        datasourceId: '',
        readerTable: ''
      },
      resetTemp() {
        this.temp = this.$options.data().temp
        this.jobJson = ''
        this.glueSource = ''
        this.timeOffset = 0
        this.timeFormatType = 'yyyy-MM-dd'
        this.partitionField = ''
      },
      executorList: '',
      jobIdList: '',
      jobProjectList: '',
      dataSourceList: '',
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
      glueTypes: [
        { value: 'BEAN', label: 'DataX任务' },
        { value: 'GLUE_SHELL', label: 'Shell任务' },
        { value: 'GLUE_PYTHON', label: 'Python任务' },
        { value: 'GLUE_POWERSHELL', label: 'PowerShell任务' }
      ],
      incrementTypes: [
        { value: 0, label: '无' },
        { value: 1, label: '主键自增' },
        { value: 2, label: '时间自增' },
        { value: 3, label: 'HIVE分区' }
      ],
      triggerNextTimes: '',
      registerNode: [],
      jobJson: '',
      glueSource: '',
      timeOffset: 0,
      timeFormatType: 'yyyy-MM-dd',
      partitionField: '',
      timeFormatTypes: [
        { value: 'yyyy-MM-dd', label: 'yyyy-MM-dd' },
        { value: 'yyyyMMdd', label: 'yyyyMMdd' },
        { value: 'yyyy/MM/dd', label: 'yyyy/MM/dd' }
      ],
      replaceFormatTypes: [
        { value: 'yyyy/MM/dd', label: 'yyyy/MM/dd' },
        { value: 'yyyy-MM-dd', label: 'yyyy-MM-dd' },
        { value: 'yyyyMMdd', label: 'yyyyMMdd' },
        { value: 'yyyyMMddHH', label: 'yyyyMMddHH' },
        { value: 'HH:mm:ss', label: 'HH:mm:ss' },
        { value: 'yyyy/MM/dd HH:mm:ss', label: 'yyyy/MM/dd HH:mm:ss' },
        { value: 'yyyy-MM-dd HH:mm:ss', label: 'yyyy-MM-dd HH:mm:ss' },
        { value: 'Timestamp', label: '时间戳' }
      ],
      statusList: [
        { value: 500, label: '失败' },
        { value: 502, label: '失败(超时)' },
        { value: 200, label: '成功' },
        { value: 0, label: '无' }
      ]
    }
  },
  created() {
    this.fetchData()
    this.getExecutor()
    this.getJobIdList()
    this.getJobProject()
    this.getDataSourceList()
  },

  methods: {
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    getExecutor() {
      job.getExecutorList().then(response => {
        const { content } = response
        this.executorList = content
      })
    },
    getJobIdList() {
      job.getJobIdList().then(response => {
        const { content } = response
          //alert(content)
        this.jobIdList = content
      })
    },
    getJobProject() {
      jobProjectApi.getJobProjectList().then(response => {
        this.jobProjectList = response.data
      })
    },
    getDataSourceList() {
      datasourceApi.getDataSourceList().then(response => {
        this.dataSourceList = response.data
      })
    },
    fetchData() {
      this.listLoading = true
        const param = Object.assign({}, this.listQuery)
        const urlJobId = this.$route.query.projectId
        if (urlJobId > 0 && !param.projectId) {
            param.projectId = urlJobId
        } else if (!urlJobId && !param.projectId) {
            param.projectId = 0
        }
      job.getList(param).then(response => {
        const { content } = response
        console.log(content)
        this.total = content.recordsTotal
        this.list = content.data
        this.listLoading = false
      })
    },
    incStartTimeFormat(vData) {
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      if (this.temp.glueType === 'BEAN' && !isJSON(this.jobJson)) {
        this.msgError('json格式错误');
        return
      }
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.temp.childJobId) {
            const auth = []
            for (const i in this.temp.childJobId) {
              auth.push(this.temp.childJobId[i].id)
            }
            this.temp.childJobId = auth.toString()
          }
          this.temp.jobJson = this.jobJson
          this.temp.glueSource = this.glueSource
          this.temp.executorHandler = this.temp.glueType === 'BEAN' ? 'executorJobHandler' : ''
          if (this.partitionField) this.temp.partitionInfo = this.partitionField + ',' + this.timeOffset + ',' + this.timeFormatType
          job.createJob(this.temp).then(() => {
            this.fetchData()
            this.dialogFormVisible = false
            this.msgSuccess("创建成功");
          })
        }
      })
    },
    handlerUpdate(row) {
      this.resetTemp()
      this.temp = Object.assign({}, row) // copy obj
      if (this.temp.jobJson) this.jobJson = JSON.parse(this.temp.jobJson)
      this.glueSource = this.temp.glueSource
      const arrchildSet = []
      const arrJobIdList = []
      if (this.jobIdList) {
        for (const n in this.jobIdList) {
          if (this.jobIdList[n].id !== this.temp.id) {
            arrJobIdList.push(this.jobIdList[n])
          }
        }
        this.JobIdList = arrJobIdList
      }

      if (this.temp.childJobId) {
        const arrString = this.temp.childJobId.split(',')
        for (const i in arrString) {
          for (const n in this.jobIdList) {
            if (this.jobIdList[n].id === arrString[i]) {
              arrchildSet.push(this.jobIdList[n])
            }
          }
        }
        this.temp.childJobId = arrchildSet
      }
      if (this.temp.partitionInfo) {
        const partition = this.temp.partitionInfo.split(',')
        this.partitionField = partition[0]
        this.timeOffset = partition[1]
        this.timeFormatType = partition[2]
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.temp.jobJson = typeof (this.jobJson) !== 'string' ? JSON.stringify(this.jobJson) : this.jobJson
      if (this.temp.glueType === 'BEAN' && !isJSON(this.temp.jobJson)) {
        this.msgError('json格式错误');
        return
      }
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.temp.childJobId) {
            const auth = []
            for (const i in this.temp.childJobId) {
              auth.push(this.temp.childJobId[i].id)
            }
            this.temp.childJobId = auth.toString()
          }
          this.temp.executorHandler = this.temp.glueType === 'BEAN' ? 'executorJobHandler' : ''
          this.temp.glueSource = this.glueSource
          if (this.partitionField) this.temp.partitionInfo = this.partitionField + ',' + this.timeOffset + ',' + this.timeFormatType
          job.updateJob(this.temp).then(() => {
            this.fetchData()
            this.dialogFormVisible = false
              this.msgSuccess("更新成功");
          })
        }
      })
    },
    handlerDelete(row) {
      this.$confirm('确定删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        job.removeJob(row.id).then(response => {
          this.fetchData()
            this.msgSuccess("删除成功");
        })
      })

      // const index = this.list.indexOf(row)
    },
    handlerExecute(row) {
      this.$confirm('确定执行吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const param = {}
        param.jobId = row.id
        param.executorParam = row.executorParam
        job.triggerJob(param).then(response => {
            this.msgSuccess("执行成功");
        })
      })
    },
    // 查看日志
    handlerViewLog(row) {
      this.$router.push({ path: '/datasync/joblog', query: { jobId: row.id }})
    },
    handlerStart(row) {
      job.startJob(row.id).then(response => {
          this.msgSuccess("启动成功");
      })
    },
    handlerStop(row) {
      job.stopJob(row.id).then(response => {
          this.msgSuccess("停止成功");
      })
    },
    changeSwitch(row) {
      row.triggerStatus === 1 ? this.handlerStart(row) : this.handlerStop(row)
    },
    nextTriggerTime(row) {
      job.nextTriggerTime(row.jobCron).then(response => {
        const { content } = response
        this.triggerNextTimes = content.join('<br>')
      })
    },
    loadById(row) {
      executor.loadById(row.jobGroup).then(response => {
        this.registerNode = []
        const { content } = response
        this.registerNode.push(content)
      })
    }
  }
}
</script>

<style>
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-dropdown + .el-dropdown {
    margin-left: 15px;
  }
</style>
