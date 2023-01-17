<template>
  <div>
    <el-form v-loading = "loading" label-position="right" label-width="150px" :model="writerForm" :rules="rules">
      <el-form-item label="数据库源：" prop="datasourceId">
        <el-select
          v-model="writerForm.datasourceId"
          filterable
          style="width: 300px;"
          @change="wDsChange"
        >
          <el-option
            v-for="item in wDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="isShowSchema() === true" label="Schema：" prop="tableSchema">
        <el-select v-model="writerForm.tableSchema" filterable style="width: 300px" @change="schemaChange">
          <el-option
            v-for="item in schemaList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据库表名：" prop="tableName">
        <el-select
          v-model="fromTableName"
          allow-create
          default-first-option
          filterable
          :filter-method="userFilter"
          :disabled="writerForm.ifCreateTable"
          style="width: 300px"
          @change="wTbChange"
        >
          <el-option
            v-for="item in wTbList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-input v-show="writerForm.ifCreateTable" v-model="writerForm.tableName" style="width: 200px;" :placeholder="readerForm.tableName" />
        <!--<el-input v-model="createTableName" style="width: 195px" />
        <el-button type="primary" @click="createTable">新增</el-button>-->
      </el-form-item>
      <el-form-item label="SQL语句：">
        <el-row>
          <el-col >
            <el-input v-model="writerForm.querySql" :autosize="{ minRows: 3, maxRows: 20}" type="textarea" placeholder="sql执行，一般用于创建表使用" style="width: 42%" />
          </el-col>
          </el-row>
        <el-row>
          <el-col >
            <el-button type="primary" size="mini" @click = "queryCreateTableSql">生成建表语句</el-button>
            <el-button type="primary" size="mini" @click = "createTable">执行</el-button>
            <el-button type="primary" size="mini" @click = "getColumnsByQuerySql">解析字段</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <div style="margin: 5px 0;" />
      <el-form-item label="字段：">
        <el-checkbox v-model="writerForm.checkAll" :indeterminate="writerForm.isIndeterminate" @change="wHandleCheckAllChange">全选</el-checkbox>
        <div style="margin: 15px 0;" />
        <el-checkbox-group v-model="writerForm.columns" @change="wHandleCheckedChange">
          <el-checkbox v-for="c in fromColumnList" :key="c" :label="c">{{ c }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="前置sql语句：">
        <el-input v-model="writerForm.preSql" placeholder="前置sql在insert之前执行" type="textarea" style="width: 42%" />
      </el-form-item>
      <el-form-item label="postSql">
        <el-input v-model="writerForm.postSql" placeholder="多个用;分隔" type="textarea" style="width: 42%" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as dsQueryApi from '@/api/datasync/metadataquery'
import { list as jdbcDsList } from '@/api/datasource/datasource'
import Bus from '../busWriter'
export default {
  name: 'RDBMSWriter',
  data() {
    return {
      jdbcDsQuery: {
        current: 1,
        size: 200,
        ascs: 'id'
      },
      readerDataSource :{
          datasourceId : undefined,
          schemaName:"",
          tableName:""
      } ,
      wDsList: [],
      schemaList: [],
      fromTableName: '',
      fromColumnList: [],
      wTbList: [],
      wAllTbList: [],
      loading: false,
      dataSource: '',
      createTableName: '',
      writerForm: {
        datasourceId: undefined,
        tableName: '',
        columns: [],
        checkAll: false,
        isIndeterminate: true,
        querySql: '',
        preSql: '',
        postSql: '',
        ifCreateTable: false,
        tableSchema: ''
      },
      readerForm: this.getReaderData(),
      rules: {
        datasourceId: [{ required: true, message: 'this is required', trigger: 'change' }],
        tableName: [{ required: true, message: 'this is required', trigger: 'change' }],
        tableSchema: [{ required: true, message: 'this is required', trigger: 'change' }]
      }
    }
  },
  watch: {
    'writerForm.datasourceId': function(oldVal, newVal) {
      if(this.writerForm.datasourceId !== 0 && this.writerForm.datasourceId !=='' && this.writerForm.datasourceId !== undefined)
      {
         this.wDsChange(this.writerForm.datasourceId);
      }
      if (this.isShowSchema()) {
        this.getSchema()
      } else {
        this.getTables('rdbmsWriter')
      }
    }
  },
  created() {
    this.getJdbcDs();
    if(this.writerForm.datasourceId !== 0 && this.writerForm.datasourceId !=='' && this.writerForm.datasourceId !== undefined)
    {
        this.wDsChange(this.writerForm.datasourceId);
    }
  },
  methods: {
    // 获取可用数据源
    getJdbcDs() {
      this.loading = true
      jdbcDsList(this.jdbcDsQuery).then(response => {
        const { records } = response.data
        this.wDsList = records
        this.loading = false
      })
    },
    userFilter(query = '') {
      let arr = this.wAllTbList.filter((item) => {
        return item.includes(query) || item.includes(query)
      })
      if (arr.length > 50) {
        this.wTbList = arr.slice(0, 50)
      } else {
        this.wTbList = arr
      }
    },
    isShowSchema()
    {
      return this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'dameng' || this.dataSource === 'sqlserver';
    },
    // 获取表名
    getTables(type) {
      if (type === 'rdbmsWriter') {
        let obj = {}
        if (this.isShowSchema()) {
          obj = {
            datasourceId: this.writerForm.datasourceId,
            tableSchema: this.writerForm.tableSchema
          }
        } else {
          obj = {
            datasourceId: this.writerForm.datasourceId
          }
        }
        // 组装
        this.loading = true
        dsQueryApi.getTables(obj).then(response => {
          this.wAllTbList = response.data
          this.loading = false
          this.userFilter()
        }).catch( e => {
          this.loading = false;
          this.msgError(e);
        })
      }
    },
    getSchema() {
      const obj = {
        datasourceId: this.writerForm.datasourceId
      }
      this.loading = true
      dsQueryApi.getTableSchema(obj).then(response => {
        this.schemaList = response.data
        this.loading = false
      }).catch( e => {
        this.loading = false;
        this.msgError(e);
      })
    },
    // schema 切换
    schemaChange(e) {
      this.writerForm.tableSchema = e
      // 获取可用表
      this.getTables('rdbmsWriter')
    },
    wDsChange(e) {
      // 清空
      this.writerForm.tableName = ''
      this.writerForm.tableSchema = ''
      this.writerForm.datasourceId = e
      this.wDsList.find((item) => {
        if (item.id === e) {
          this.dataSource = item.datasource
        }
      })
      Bus.dataSourceId = e
      this.$emit('selectDataSource', this.dataSource)
    },
    // 获取表字段
    getColumns() {
      const obj = {
        datasourceId: this.writerForm.datasourceId,
        tableName: this.writerForm.tableName,
        tableSchema: this.writerForm.tableSchema
      }
      this.loading = true
      dsQueryApi.getColumns(obj).then(response => {
        this.fromColumnList = response.data
        this.writerForm.columns = response.data
        this.writerForm.checkAll = true
        this.writerForm.isIndeterminate = false
        this.loading = false
      }).catch( e => {
        this.loading = false;
        this.msgError(e);
      })
    },
      getColumnsByQuerySql() {
          const obj = {
              datasourceId: this.writerForm.datasourceId,
              querySql: this.writerForm.querySql
          }
          if (this.writerForm.querySql !== '') {
              this.loading = true
              dsQueryApi.getColumnsByQuerySql(obj).then(response => {
                  this.fromColumnList = response.data
                  this.writerForm.columns = response.data
                  this.writerForm.checkAll = true
                  this.writerForm.isIndeterminate = false
                  this.loading = false
              }).catch( e => {
                this.loading = false;
                this.msgError(e);
              })
          }
      },
    // 表切换
    wTbChange(t) {
      this.writerForm.tableName = t
      this.fromColumnList = []
      this.writerForm.columns = []
      this.getColumns('writer')
    },
    wHandleCheckAllChange(val) {
      this.writerForm.columns = val ? this.fromColumnList : []
      this.writerForm.isIndeterminate = false
    },
    wHandleCheckedChange(value) {
      const checkedCount = value.length
      this.writerForm.checkAll = checkedCount === this.fromColumnList.length
      this.writerForm.isIndeterminate = checkedCount > 0 && checkedCount < this.fromColumnList.length
    },
    getData() {
      if (Bus.dataSourceId) {
        this.writerForm.datasourceId = Bus.dataSourceId
      }
      return this.writerForm
    },
    getReaderData() {
      return this.$parent.getReaderData()
    },
    getTableName() {
      return this.fromTableName
    },
    createTable() {
      const obj = {
        datasourceId: this.writerForm.datasourceId,
        querySql: this.writerForm.querySql
      }
      if(this.writerForm.querySql !== '')
      {
          dsQueryApi.createTable(obj).then(response => {
              if(response.data !== "")
                  this.msgError(response.data);
              else
                  this.msgSuccess("执行成功")
          }).catch(() => console.log('promise catch err'))
      }
    },
      queryCreateTableSql() {
          const obj = {
              datasourceId: this.readerDataSource.datasourceId,
              schema: this.readerDataSource.schemaName,
              tablename: this.readerDataSource.tableName,
              targetdatasourceId: this.writerForm.datasourceId,
              targetschema:this.writerForm.tableSchema
          }
          dsQueryApi.queryCreateTableSql(obj).then(response => {
              console.log(response)
              this.writerForm.querySql = response.data
              this.msgSuccess("SQL创建成功")
          }).catch(() => console.log('promise catch err'))
      }
  }
}
</script>
