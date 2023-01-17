<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="数据源名称" prop="datasourceName">
        <el-input
          v-model="listQuery.datasourceName"
          clearable
          placeholder="请输入数据源名称"
          style="width: 240px;"
          size="small"
          @keyup.enter.native="fetchData"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="fetchData">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCreate">添加</el-button>
      </el-form-item>
    </el-form>
      <!-- <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">
        reviewer
      </el-checkbox> -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"

      highlight-current-row
    >
      <!-- <el-table-column align="center" label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index }}</template>
      </el-table-column> -->
      <el-table-column label="数据源类型" width="200" align="center">
        <template slot-scope="scope">{{ scope.row.datasource }}</template>
      </el-table-column>
      <el-table-column label="数据源名称" width="150" align="center">
        <template slot-scope="scope">{{ scope.row.datasourceName }}</template>
      </el-table-column>
      <el-table-column label="数据源分组" width="200" align="center"  prop="datasourceGroup" :formatter="formatDatasourceGroup">

      </el-table-column>
      <!--<el-table-column label="用户名" width="150" align="center">
        <template slot-scope="scope">{{ scope.row.jdbcUsername ? scope.row.jdbcUsername:'-' }}</template>
      </el-table-column>-->
      <el-table-column label="jdbc连接串" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.jdbcUrl ? scope.row.jdbcUrl:'-' }}</template>
      </el-table-column>
      <!-- <el-table-column label="jdbc驱动类" width="200" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.jdbcDriverClass ? scope.row.jdbcDriverClass:'-' }}</template>
      </el-table-column>-->
      <el-table-column label="ZK/FE地址" width="200" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.zkAdress ? scope.row.zkAdress:'-' }}</template>
      </el-table-column>
      <!--
      <el-table-column label="数据库名" width="200" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.databaseName ? scope.row.databaseName:'-' }}</template>
      </el-table-column>-->
      <el-table-column label="备注" width="150" align="center">
        <template slot-scope="scope">{{ scope.row.comments }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleUpdate(scope.row)">
            修改
          </el-button>
          <el-button v-if="scope.row.status!='deleted'" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">
            删除
          </el-button>
          <el-button v-if="scope.row.datasource!='presto'" type="text" icon="el-icon-refresh" size="mini" @click="handleCreate">同步到presto</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.current"
      :limit.sync="listQuery.size"
      @pagination="fetchData"
    />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="600px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px">
        <el-form-item label="数据源" prop="datasource">
          <el-select
            v-model="temp.datasource"
            placeholder="数据源"
            style="width: 200px"
            @change="selectDataSource(temp.datasource)"
          >
            <el-option v-for="item in dataSources" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据源名称" prop="datasourceName">
          <el-input v-model="temp.datasourceName" placeholder="数据源名称" style="width: 200px" />
        </el-form-item>
        <el-form-item label="数据源分组" prop="datasourceGroup">
          <el-select
            v-model="temp.datasourceGroup"
            placeholder="数据源分组"
            style="width: 200px"
          >
            <el-option
              v-for="dict in datasourceGroupList"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="jdbc" label="用户名">
          <el-input v-model="temp.jdbcUsername" placeholder="用户名" style="width: 200px" />
        </el-form-item>
        <el-form-item v-if="visible" v-show="jdbc" label="密码">
          <el-input v-model="temp.jdbcPassword" type="password" placeholder="密码" style="width: 200px">
            <i slot="suffix" title="显示密码" style="cursor:pointer" class="el-icon-view" @click="changePass('show')" />
          </el-input>
        </el-form-item>
        <el-form-item v-show="jdbc" v-else label="密码">
          <el-input v-model="temp.jdbcPassword" type="text" placeholder="密码" style="width: 200px">
            <i slot="suffix" title="隐藏密码" style="cursor:pointer" class="el-icon-check" @click="changePass('hide')" />
          </el-input>
        </el-form-item>
        <el-form-item v-if="jdbc" label="jdbc url" prop="jdbcUrl">
          <el-input
            v-model="temp.jdbcUrl"
            :autosize="{ minRows: 3, maxRows: 6}"
            type="textarea"
            placeholder="jdbc url"
            style="width: 90%"
          />
        </el-form-item>
        <el-form-item v-if="mongodb" label="地址" prop="jdbcUrl">
          <el-input
            v-model="temp.jdbcUrl"
            :autosize="{ minRows: 3, maxRows: 6}"
            type="textarea"
            placeholder="127.0.0.1:27017"
            style="width: 60%"
          />
        </el-form-item>
        <el-form-item v-if="jdbc" label="jdbc驱动类" prop="jdbcDriverClass">
          <el-input v-model="temp.jdbcDriverClass" placeholder="jdbc驱动类" style="width: 60%" />
        </el-form-item>
        <el-form-item v-if="hbase" label="ZK地址" prop="zkAdress">
          <el-input v-model="temp.zkAdress" placeholder="127.0.0.1:2181" style="width: 60%" />
        </el-form-item>
        <el-form-item v-if="doris" label="FE地址" prop="zkAdress">
          <el-input v-model="temp.zkAdress" placeholder="127.0.0.1:8030 多个地址以;分割" style="width: 60%" />
        </el-form-item>
        <el-form-item v-if="mongodb" label="数据库名称" prop="databaseName">
          <el-input v-model="temp.databaseName" placeholder="数据库名称" style="width: 60%" />
        </el-form-item>
        <el-form-item label="注释">
          <el-input
            v-model="temp.comments"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
            style="width: 60%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确认
        </el-button>
        <el-button type="primary" @click="testDataSource()">
          测试连接
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogPluginVisible" title="Reading statistics">
      <el-table :data="pluginData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel" />
        <el-table-column prop="pv" label="Pv" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as datasourceApi from '@/api/datasource/datasource'
import { parseTime } from '@/utils'

export default {
  name: 'JdbcDatasource',
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
        ascs:'id'
      },
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '修改',
        create: '新增'
      },
      rules: {
        datasourceName: [{ required: true, message: 'this is required', trigger: 'blur' }],
        datasourceGroup: [{ required: true, message: 'this is required', trigger: 'blur' }],
        jdbcUsername: [{ required: true, message: 'this is required', trigger: 'blur' }],
        jdbcPassword: [{ required: true, message: 'this is required', trigger: 'blur' }],
        jdbcUrl: [{ required: true, message: 'this is required', trigger: 'blur' }],
        jdbcDriverClass: [{ required: true, message: 'this is required', trigger: 'blur' }],
        datasource: [{ required: true, message: 'this is required', trigger: 'change' }],
        zkAdress: [{ required: true, message: 'this is required', trigger: 'blur' }],
        databaseName: [{ required: true, message: 'this is required', trigger: 'blur' }]
      },
      temp: {
        id: undefined,
        datasourceName: '',
        datasourceGroup: '1',
        jdbcUsername: '',
        jdbcPassword: '',
        jdbcUrl: '',
        jdbcDriverClass: '',
        comments: '',
        datasource: '',
        zkAdress: '',
        databaseName: ''
      },
      visible: true,
      dataSources: [
        { value: 'mysql', label: 'mysql' },
        { value: 'doris', label: 'doris' },
        { value: 'oracle', label: 'oracle' },
        { value: 'dameng', label: 'dameng' },
        { value: 'postgresql', label: 'postgresql' },
        { value: 'greenplum', label: 'greenplum' },
        { value: 'sqlserver', label: 'sqlserver' },
        { value: 'hive', label: 'hive' },
        { value: 'hbase', label: 'hbase' },
        { value: 'mongodb', label: 'mongodb' },
        { value: 'clickhouse', label: 'clickhouse' },
        { value: 'presto', label: 'presto' }
      ],
      datasourceGroupList:[],
      jdbc: true,
      hbase: false,
      mongodb: false,
      doris: false
    }
  },
  created() {
    this.fetchData();
      this.getDicts("sys_datasource_group").then(response => {
          this.datasourceGroupList = response.data;
      });

  },
  methods: {
      formatDatasourceGroup(row, column, cellValue) {
          var ret = ''  //你想在页面展示的值
          this.datasourceGroupList.find((item) => {
              if (item.dictValue === cellValue) {
                  ret = item.dictLabel
              }
          })
          return ret
      },
    selectDataSource(datasource) {
      if (datasource === 'mysql' || datasource === 'doris') {
        this.temp.jdbcUrl = 'jdbc:mysql://{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'com.mysql.jdbc.Driver'
      } else if (datasource === 'oracle') {
        this.temp.jdbcUrl = 'jdbc:oracle:thin:@//{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'oracle.jdbc.OracleDriver'
      } else if (datasource === 'dameng') {
        this.temp.jdbcUrl = 'jdbc:dm://{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'dm.jdbc.driver.DmDriver'
      } else if (datasource === 'postgresql' || datasource === 'greenplum') {
        this.temp.jdbcUrl = 'jdbc:postgresql://{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'org.postgresql.Driver'
      } else if (datasource === 'sqlserver') {
        this.temp.jdbcUrl = 'jdbc:sqlserver://{host}:{port};DatabaseName={database}'
        this.temp.jdbcDriverClass = 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
      } else if (datasource === 'clickhouse') {
        this.temp.jdbcUrl = 'jdbc:clickhouse://{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'ru.yandex.clickhouse.ClickHouseDriver'
      } else if (datasource === 'hive') {
        this.temp.jdbcUrl = 'jdbc:hive2://{host}:{port}/{database}'
        this.temp.jdbcDriverClass = 'org.apache.hive.jdbc.HiveDriver'
        this.hbase = this.mongodb = false
        this.jdbc = true
      }
      else if (datasource === 'presto') {
          this.temp.jdbcUrl = 'jdbc:presto://{host}:{port}/{catalog}'
          this.temp.jdbcDriverClass = 'com.facebook.presto.jdbc.PrestoDriver'
      }
      this.getShowStrategy(datasource)
    },
    fetchData() {
      this.listLoading = true
      datasourceApi.list(this.listQuery).then(response => {
        this.total = response.data.total
        this.list = response.data.records
        this.listLoading = false
      })
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        datasourceName: '',
        datasourceGroup: '1',
        jdbcUsername: '',
        jdbcPassword: '',
        jdbcUrl: '',
        jdbcDriverClass: '',
        comments: ''
      }
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
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          datasourceApi.created(this.temp).then(() => {
            this.fetchData()
            this.dialogFormVisible = false
              this.msgSuccess("创建成功")
          })
        }
      })
    },
    testDataSource() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          datasourceApi.test(this.temp).then(response => {
            if (response.data === false) {
                this.msgError("连接失败");
            } else {
                this.msgSuccess("连接成功");
            }
          }).catch( e => {
            this.msgError(e);
          })
        }
      })
    },
    handleUpdate(row) {
      this.getShowStrategy(row.datasource)
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          datasourceApi.updated(tempData).then(() => {
            this.fetchData();
            this.dialogFormVisible = false;
            this.msgSuccess("更新成功");
          })
        }
      })
    },
    getShowStrategy(datasource) {
      if (datasource === 'hbase') {
        this.jdbc = this.mongodb = false
        this.hbase = true
      } else if (datasource === 'mongodb') {
        this.jdbc = this.hbase = false
        this.mongodb = true
        this.temp.jdbcUrl = 'mongodb://[username:password@]host1[:port1][,...hostN[:portN]]][/[database][?options]]'
      } else if (datasource === 'doris') {
        this.doris = this.jdbc = true
        this.hbase = this.mongodb = false
      } else {
        this.hbase = this.mongodb = false
        this.jdbc = true
      }
    },
    handleDelete(row) {
      //console.log('删除')
      const idList = []
      idList.push(row.id)
      // 拼成 idList=xx
      // 多个比较麻烦，这里不处理
        this.$confirm('是否确认删除:"' + row.datasourceName + '?', "警告", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(function() {
            datasourceApi.deleted({ idList: row.id });
        }).then(() => {
            this.fetchData();
            this.msgSuccess("删除成功");
        }).catch( e => {
          this.msgError(e);
        })
      // const index = this.list.indexOf(row)
    },
    handleFetchPv(id) {
      datasourceApi.fetched(id).then(response => {
        this.pluginData = response
        this.dialogPvVisible = true
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    },
    changePass(value) {
      this.visible = !(value === 'show')
    }
  }
}
</script>
