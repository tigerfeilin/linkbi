<template>
  <div class="app-container">
    <el-form v-loading = "loading" label-position="right" label-width="120px" :model="readerForm" :rules="rules">
      <el-form-item label="数据库源：" prop="datasourceId">
        <el-select v-model="readerForm.datasourceId" filterable style="width: 300px" @change="rDsChange">
          <el-option
            v-for="item in rDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="isShowSchema() === true" label="Schema：" prop="tableSchema">
        <el-select v-model="readerForm.tableSchema" allow-create default-first-option filterable style="width: 300px" @change="schemaChange">
          <el-option
            v-for="item in schemaList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据库表名：" prop="tableName">
        <el-select v-model="readerForm.tableName" allow-create default-first-option filterable :filter-method="userFilter" style="width: 300px" @change="rTbChange">
          <el-option v-for="item in rTbList" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="SQL语句：">
        <el-row>
          <el-col >
            <el-input v-model="readerForm.querySql" :autosize="{ minRows: 3, maxRows: 20}" type="textarea" placeholder="sql查询，一般用于多表关联查询时才用" style="width: 42%" />
          </el-col>
          <el-col :span="8">
            <el-button type="primary" size="mini" @click.prevent="getColumns('reader')">解析字段</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="切分字段：">
        <el-input v-model="readerForm.splitPk" placeholder="切分主键" style="width: 13%" />
      </el-form-item>
      <el-form-item label="表所有字段：">
        <el-checkbox
          v-model="readerForm.checkAll"
          :indeterminate="readerForm.isIndeterminate"
          @change="rHandleCheckAllChange"
        >全选
        </el-checkbox>
        <div style="margin: 15px 0;" />
        <el-checkbox-group v-model="readerForm.columns" @change="rHandleCheckedChange">
          <el-checkbox v-for="c in rColumnList" :key="c" :label="c">{{ c }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="where条件：" prop="where">
        <el-input v-model="readerForm.where" placeholder="where条件，不需要再加where" type="textarea" style="width: 42%" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as dsQueryApi from '@/api/datasync/metadataquery'
import { list as jdbcDsList } from '@/api/datasource/datasource'
import Bus from '../busReader'

export default {
  name: 'RDBMSReader',
  data() {
    return {
      jdbcDsQuery: {
        current: 1,
        size: 200,
        ascs: 'id'
      },
      rDsList: [],
      rTbList: [],
      rAllTbList: [],
      schemaList: [],
      rColumnList: [],
      loading: false,
      active: 1,
      customFields: '',
      customType: '',
      customValue: '',
      dataSource: '',
      readerForm: {
        datasourceId: undefined,
        tableName: '',
        columns: [],
        where: '',
        querySql: '',
        checkAll: false,
        isIndeterminate: true,
        splitPk: '',
        tableSchema: ''
      },
      rules: {
        datasourceId: [{ required: true, message: 'this is required', trigger: 'change' }],
        tableName: [{ required: true, message: 'this is required', trigger: 'change' }],
        tableSchema: [{ required: true, message: 'this is required', trigger: 'change' }]
      }
    }
  },
  watch: {
    'readerForm.datasourceId': function(oldVal, newVal) {
      if(this.readerForm.datasourceId !== 0 && this.readerForm.datasourceId !=='' && this.readerForm.datasourceId !== undefined)
      {
         this.rDsChange(this.readerForm.datasourceId);
      }
      if (this.isShowSchema()) {
        this.getSchema()
      } else {
        this.getTables('rdbmsReader')
      }
    }
  },
  created() {
    this.getJdbcDs();
    if(this.readerForm.datasourceId !== 0 && this.readerForm.datasourceId !=='' && this.readerForm.datasourceId !== undefined)
    {
       this.rDsChange(this.readerForm.datasourceId);
    }
  },
  methods: {
    // 获取可用数据源
    getJdbcDs() {
      this.loading = true
      jdbcDsList(this.jdbcDsQuery).then(response => {
        const { records } = response.data
        this.rDsList = records
        this.loading = false
      }).catch( e => {
        this.loading = false;
        this.msgError(e);
      });
    },
    userFilter(query = '') {
      let arr = this.rAllTbList.filter((item) => {
        return item.includes(query)
      })
      if (arr.length > 50) {
        this.rTbList = arr.slice(0, 50)
      } else {
        this.rTbList = arr
      }
    },
    isShowSchema()
    {
      return this.dataSource === 'postgresql' || this.dataSource === 'greenplum'  || this.dataSource === 'oracle' || this.dataSource === 'dameng' || this.dataSource === 'sqlserver';
    },
    // 获取表名
    getTables(type) {
      if (type === 'rdbmsReader') {
        let obj = {}
        if (this.isShowSchema()) {
          obj = {
            datasourceId: this.readerForm.datasourceId,
            tableSchema: this.readerForm.tableSchema
          }
        } else {
          obj = {
            datasourceId: this.readerForm.datasourceId
          }
        }
        // 组装
        this.loading = true;
        dsQueryApi.getTables(obj).then(response => {
          if (response) {
            this.rAllTbList = response.data
            this.loading = false
            this.userFilter()
          }
        }).catch( e => {
          this.loading = false;
          this.msgError(e);
        })
      }
    },
    getSchema() {
      const obj = {
        datasourceId: this.readerForm.datasourceId
      }
      dsQueryApi.getTableSchema(obj).then(response => {
        this.schemaList = response.data
      }).catch( e => {
        this.loading = false;
        this.msgError(e);
      })
    },
    // schema 切换
    schemaChange(e) {
      this.readerForm.tableSchema = e
      // 获取可用表
      this.getTables('rdbmsReader')
    },
    // reader 数据源切换
    rDsChange(e) {
      // 清空
      this.readerForm.tableName = ''
      this.readerForm.tableSchema = ''
      this.readerForm.datasourceId = e
      if(this.rDsList.length === 0)
      {
          jdbcDsList(this.jdbcDsQuery).then(response => {
              const { records } = response.data
              this.rDsList = records
              this.rDsList.find((item) => {
                  if (item.id === e) {
                      this.dataSource = item.datasource
                      if (this.isShowSchema()) {
                          this.getSchema()
                      } else {
                          this.getTables('rdbmsReader')
                      }
                  }
              })
          })
      }
      else
      {
          this.rDsList.find((item) => {
              if (item.id === e) {
                  this.dataSource = item.datasource
              }
          })
      }

      Bus.dataSourceId = e
      this.$emit('selectDataSource', this.dataSource)
    },
    getTableColumns() {
      const obj = {
        datasourceId: this.readerForm.datasourceId,
        tableName: this.readerForm.tableName,
        tableSchema:this.readerForm.tableSchema,
      }
      this.loading = true;
      dsQueryApi.getColumns(obj).then(response => {
        this.rColumnList = response.data
        this.readerForm.columns = response.data
        this.readerForm.checkAll = true
        this.readerForm.isIndeterminate = false
        this.loading = false;
      }).catch( e => {
        this.loading = false;
        this.msgError(e);
      })
    },
    getColumnsByQuerySql() {
      const obj = {
        datasourceId: this.readerForm.datasourceId,
        querySql: this.readerForm.querySql
      }
      dsQueryApi.getColumnsByQuerySql(obj).then(response => {
        this.rColumnList = response.data
        this.readerForm.columns = response.data
        this.readerForm.checkAll = true
        this.readerForm.isIndeterminate = false
      })
    },
    // 获取表字段
    getColumns(type) {
      if (type === 'reader') {
        if (this.readerForm.querySql !== '') {
          this.getColumnsByQuerySql()
        } else {
          this.getTableColumns()
        }
      }
    },
    // 表切换
    rTbChange(t) {
      this.readerForm.tableName = t
      this.rColumnList = []
      this.readerForm.columns = []
      this.getColumns('reader')
    },
    rHandleCheckAllChange(val) {
      this.readerForm.columns = val ? this.rColumnList : []
      this.readerForm.isIndeterminate = false
    },
    rHandleCheckedChange(value) {
      const checkedCount = value.length
      this.readerForm.checkAll = checkedCount === this.rColumnList.length
      this.readerForm.isIndeterminate = checkedCount > 0 && checkedCount < this.rColumnList.length
    },
    getData() {
      if (Bus.dataSourceId) {
        this.readerForm.datasourceId = Bus.dataSourceId
      }
      return this.readerForm
    }
  }
}
</script>
