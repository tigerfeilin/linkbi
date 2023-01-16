<template>
  <!-- 导入表 -->
  <el-dialog title="导入表" :visible.sync="visible" width="800px" top="5vh" append-to-body>
    <el-form :model="listQuery" ref="queryForm" :inline="true">
      <el-form-item label="数据源" prop="datasourceId">
        <el-select v-model="listQuery.datasourceId" filterable @change="rDsChange" style="width: 300px">
          <el-option
            v-for="item in rDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="dataSource==='postgresql' || dataSource==='greenplum' || dataSource==='oracle' ||dataSource==='sqlserver' " label="Schema" prop="tableSchema">
        <el-select v-model="listQuery.tableSchema" allow-create default-first-option filterable style="width: 300px" >
          <el-option
            v-for="item in schemaList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="listQuery.tableName"
          placeholder="请输入表名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
          style="width: 300px"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <!--<el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
      </el-form-item>
    </el-form>
    <el-row>
      <el-table v-loading="loading"  @row-click="clickRow" ref="table" :data="dbTableList" @selection-change="handleSelectionChange" height="260px">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="tableName" label="表名称" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="tableType" label="类型" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="tableComment" label="描述" :show-overflow-tooltip="true"></el-table-column>
      </el-table>
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="listQuery.current"
        :limit.sync="listQuery.size"
        @pagination="getList"
      />
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleImportTable">确 定</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import * as dsQueryApi from '@/api/datasync/metadataquery'
import { list as jdbcDsList } from '@/api/datasource/datasource'
export default {
  data() {
    return {
      // 遮罩层
      visible: false,
      // 选中数组值
      rDsList:[],
      schemaList: [],
      // 总条数
      total: 0,
      dataSource: '',
      // 表数据
      dbTableList: [],
      tables: [],
      loading:false,
      // 查询参数
      listQuery: {
        current: 1,
        size: 10,
        tableName: undefined,
        datasourceId: undefined,
        tableSchema: '',
        tables: [],
      },
        jdbcDsQuery: {
            current: 1,
            size: 200,
            ascs: 'id'
        },
    };
  },
    watch: {
        'listQuery.datasourceId': function() {
            if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
            {
                this.rDsChange(this.listQuery.datasourceId);
            }
            if (this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                this.getSchema()
            }
        }
    },
    created() {
        this.getJdbcDs();
        if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
        {
            this.rDsChange(this.listQuery.datasourceId);
        }
    },
  methods: {
    // 显示弹框
    show() {
          //this.getList();
          this.visible = true;
    },
    getList()
    {
        this.loading = true
        dsQueryApi.getImportTables(this.listQuery).then(response => {
            this.dbTableList = response.data.rows
            this.total = response.data.total
            this.loading = false
        }).catch(e =>{
            this.msgError(e);
            this.loading = false
        })
    },
    clickRow(row) {
      this.$refs.table.toggleRowSelection(row);
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.tables = selection.map(item => item.tableName);
    },
      getJdbcDs() {
          this.loading = true
          jdbcDsList(this.jdbcDsQuery).then(response => {
              const { records } = response.data
              this.rDsList = records
              this.loading = false
          })
      },
      getSchema() {
          const obj = {
              datasourceId: this.listQuery.datasourceId
          }
          this.loading = true
          dsQueryApi.getTableSchema(obj).then(response => {
              this.schemaList = response.data
              this.loading = false
          }).catch(e =>{
              this.msgError(e);
              this.loading = false
          })
      },
      // schema 切换
      rDsChange(e) {
          // 清空
          this.listQuery.tableName = ''
          this.listQuery.tableSchema = ''
          this.listQuery.datasourceId = e
          if(this.rDsList.length === 0)
          {
              jdbcDsList(this.jdbcDsQuery).then(response => {
                  const { records } = response.data
                  this.rDsList = records
                  this.rDsList.find((item) => {
                      if (item.id === e) {
                          this.dataSource = item.datasource
                          if (this.dataSource === 'postgresql' || dataSource==='greenplum' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                              this.getSchema()
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
          if (this.dataSource === 'postgresql' || dataSource==='greenplum' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
              this.getSchema()
          }
      },
    /** 搜索按钮操作 */
    handleQuery() {
      this.listQuery.current = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 导入按钮操作 */
    handleImportTable() {
        this.listQuery.tables = this.tables.join(",")
        dsQueryApi.importTable(this.listQuery).then(res => {
        this.msgSuccess(res.msg);
        if (res.code === 200) {
          this.visible = false;
          this.$emit("ok");
        }
      });
    }
  }
};
</script>
