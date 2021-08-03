<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="数据源" prop="datasourceId">
        <el-select v-model="listQuery.datasourceId" filterable clearable size="small" >
          <el-option
            v-for="item in rDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="表分类" prop="tableComment">
        <el-select v-model="listQuery.tableType" placeholder="表分类" clearable size="small">
          <el-option
            v-for="dict in typeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="表名称" prop="tableComment">
        <el-input
          v-model="listQuery.tableName"
          placeholder="请输入表名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-download"
          size="mini"
          @click="handleGenTable"
          v-hasPermi="['metadata:table:export']"
        >下载字典</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          icon="el-icon-upload"
          size="mini"
          @click="openImportTable"
          v-hasPermi="['metadata:table:import']"
        >导入</el-button>
      </el-col>
      <!--
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleEditTable"
          v-hasPermi="['tool:gen:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['metadata:table:remove']"
        >删除</el-button>
      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" >
      <!--<el-table-column type="selection" width="55"></el-table-column>@selection-change="handleSelectionChange"-->
      <el-table-column label="序号" type="index" width="100" align="center">
        <template slot-scope="scope">
          <span>{{(listQuery.current - 1) * listQuery.size + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="表名称"
        align="center"
        prop="tableName"
        :show-overflow-tooltip="true"
        width="250"
      />
      <el-table-column
        label="中文表名"
        align="center"
        prop="tableChName"
        :show-overflow-tooltip="true"
        width="250"
      />
      <el-table-column
        label="表描述"
        align="center"
        prop="tableComment"
        :show-overflow-tooltip="true"
        width="250"
      />
      <el-table-column
        label="分类"
        align="center"
        prop="tableType"
        :formatter="formatTableType"
        :show-overflow-tooltip="true"
        width="100"
      />
      <el-table-column
        label="生命周期"
        align="center"
        prop="tableCycle"
        :formatter="formatTableCycle"
        :show-overflow-tooltip="true"
        width="100"
      />
      <el-table-column
        label="状态"
        align="center"
        prop="tableStatus"
        :formatter="formatTableStatus"
        :show-overflow-tooltip="true"
        width="100"
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
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['metadata:table:view']"
          >浏览</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleEditTable(scope.row)"
            v-hasPermi="['metadata:table:edit']"
          >编辑</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['metadata:table:delete']"
          >删除</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-refresh"
            @click="handleSynchDb(scope.row)"
            v-hasPermi="['metadata:table:edit']"
          >同步</el-button>
          <el-button type="text" icon="el-icon-medal-1" size="small" @click="handleImportMarket(scope.row)">归集</el-button>
          <!--<el-button
            type="text"
            size="small"
            icon="el-icon-download"
            @click="handleGenTable(scope.row)"
            v-hasPermi="['metadata:table:export']"
          >订阅</el-button>-->
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
    <!-- 数据归集 -->
    <el-dialog title="数据归集" :visible.sync="open" width="40%" top="5vh" append-to-body>
      <el-form ref="dataForm"  :model="marketQuery" label-position="left" label-width="100px">
        <!--<el-form-item label="查询类型" prop="queryType">
          <el-select v-model="marketQuery.queryType" filterable size="small" >
            <el-option
              v-for="dict in queryTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>-->
        <el-form-item label="视图类型" prop="viewType">
          <el-select v-model="marketQuery.viewType" filterable size="small" >
            <el-option
              v-for="dict in viewTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">
          取消
        </el-button>
        <el-button type="primary" @click="importMarket()">
          确认
        </el-button>
      </div>
    </el-dialog>
    <import-table ref="import" @ok="handleQuery" />
  </div>
</template>

<script>
import * as dsQueryApi from '@/api/datasync/metadataquery'
import importTable from "./importTable";
import { downLoadZip } from "@/utils/zipdownload";
import { list as jdbcDsList } from '@/api/datasource/datasource'
export default {
  name: "MetaDataTable",
  components: { importTable },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 唯一标识符
      uniqueId: "",
      open:false,
      // 选中数组
      ids: [],
      rDsList: [],
      // 选中表数组
      typeOptions: [],
      cycleTypeOptions:[],
      queryTypeOptions:[],
      viewTypeOptions:[],
      tableNames: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表数据
      tableList: [],
      // 日期范围
      dateRange: "",
      // 查询参数
      listQuery: {
        current: 1,
        size: 10,
        datasourceId:undefined,
        tableName: undefined,
        tableComment: undefined,
        tableType: undefined
      },
      marketQuery: {
          datasourceId:undefined,
          schemaName:undefined,
          modeName:undefined,
          modeType:undefined,
          queryType:'1',
          querySQL:undefined,
          viewType:'1'
      },
      jdbcDsQuery:{
          current: 1,
          size: 200,
          ascs: 'id',
          datasource_group: '1',
      },
    };
  },
  created() {
    this.getList();
    this.getJdbcDs();
    this.getDicts("sys_table_type").then(response => {
        this.typeOptions = response.data;
    });
    this.getDicts("sys_table_cycle_list").then(response => {
        this.cycleTypeOptions = response.data;
    });
    this.getDicts("sys_datasource_group").then(response => {
        this.queryTypeOptions = response.data;
    });
    this.getDicts("sys_dataview_type").then(response => {
        this.viewTypeOptions = response.data;
    });
    if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
    {
          this.rDsChange(this.listQuery.datasourceId);
    }

  },
  activated() {
    const time = this.$route.query.t;
    if (time != null && time !== this.uniqueId) {
      this.uniqueId = time;
      this.resetQuery();
    }
  },
  methods: {
      formatTableType(row, column, cellValue) {
          var ret = ''  //你想在页面展示的值
          this.typeOptions.find((item) => {
              if (item.dictValue === cellValue) {
                  ret = item.dictLabel
              }
          })
          return ret
      },
      formatTableCycle(row, column, cellValue) {
          var ret = ''  //你想在页面展示的值
          this.cycleTypeOptions.find((item) => {
              if (item.dictValue === cellValue) {
                  ret = item.dictLabel
              }
          })
          return ret
      },
      formatTableStatus(row, column, cellValue) {
          var ret = ''  //你想在页面展示的值
          if (cellValue === '1') {
              ret = "正常"  //根据自己的需求设定
          } else {
              ret = "异常"
          }
          return ret;
      },
    /** 查询表集合 */
    getList() {
      this.loading = true;
        dsQueryApi.listTables(this.listQuery).then(response => {
          this.tableList = response.data.rows;
          this.total = response.data.total;
          this.loading = false;
        }
      );
    },
    getJdbcDs() {
          this.loading = true
          jdbcDsList(this.jdbcDsQuery).then(response => {
              const { records } = response.data
              this.rDsList = records
              this.loading = false
          })
    },
      // schema 切换
      rDsChange(e) {
          // 清空
          this.listQuery.tableName = ''
          this.listQuery.tableSchema = ''
          this.listQuery.datasourceId = e
      },
    /** 搜索按钮操作 */
    handleQuery() {
      this.listQuery.current = 1;
      this.getList();
    },
    /** 数据订阅操作 */
    handleGenTable(row) {
      const tableNames = row.tableName || this.tableNames;
      if (tableNames === "") {
        this.msgError("请选择要生成的数据");
        return;
      }
    },
      /** 数据归集操作 */
      handleImportMarket(row) {
          const tableId = row.id;
          this.loading = true
          dsQueryApi.listTableById({tableId}).then(response => {
              const obj = {
                  datasourceId: response.data.datasourceId,
                  schema:response.data.databaseName,
                  tablename:response.data.tableName
              }
              dsQueryApi.queryModelSql(obj).then(sql => {
                  this.marketQuery.datasourceId = response.data.datasourceId
                  this.marketQuery.schemaName = response.data.databaseName
                  this.marketQuery.modeName = response.data.tableName
                  this.marketQuery.modeType = response.data.tableType
                  this.marketQuery.querySql = sql.data
                  this.open = true
              }).catch(e =>{
                  this.msgError(e);
                  this.loading = false
              })
              this.loading = false
          }).catch(e =>{
              this.msgError(e);
              this.loading = false
          })
      },
      /** 数据归集操作 */
      importMarket() {
          this.loading = true;
          dsQueryApi.insertMarket(this.marketQuery).then(response => {
              this.loading = false
              if(Number(response.data) < 0){
                  this.msgError("表信息错误或已归集该表");
              }
              else{
                  this.open = false
                  this.msgSuccess("归集成功")
              }
          }).catch(e =>{
              this.msgError(e);
              this.loading = false
          })
      },
    /** 同步数据库操作 */
    handleSynchDb(row) {
      const tableId = row.id;
      const tableName = row.tableName;
      this.$confirm('确认要强制同步"' + tableName + '"表结构吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
          return dsQueryApi.syncTable({tableId});
      }).then(() => {
          this.msgSuccess("同步成功");
      })
    },
    /** 打开导入表弹窗 */
    openImportTable() {
      this.$refs.import.show();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 预览按钮 */
    handlePreview(row) {
        const tableId = row.id;
        this.$router.push("/metadata/view/" + tableId);
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.tableNames = selection.map(item => item.tableName);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 修改按钮操作 */
    handleEditTable(row) {
      const tableId = row.id;
      this.$router.push("/metadata/edit/" + tableId);
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const tableId = row.id;
      const tableNames = row.tableName || this.tableNames;
      this.$confirm('是否确认删除表:"' + tableNames + '"?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
          return dsQueryApi.removeTable({tableId});
      }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
      })
    }
  }
};
</script>
