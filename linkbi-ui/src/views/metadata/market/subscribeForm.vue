<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="数据源" prop="datasourceId">
        <el-select v-model="listQuery.datasourceId" filterable clearable size="mini">
          <el-option
            v-for="item in rDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据分类" prop="tableComment">
        <el-select v-model="listQuery.tableType" placeholder="表分类" filterable clearable size="mini">
          <el-option
            v-for="dict in typeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="视图类型" prop="tableComment">
        <el-select v-model="listQuery.viewType" placeholder="视图类型" filterable clearable size="mini">
          <el-option
            v-for="dict in viewTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据名称" prop="tableComment">
        <el-input
          v-model="listQuery.tableName"
          placeholder="请输入数据名称"
          clearable
          size="mini"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
      <!--<right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
    </el-form>

    <el-table v-loading="loading" :data="tableList" >
      <!--<el-table-column type="selection" width="55"></el-table-column>@selection-change="handleSelectionChange"-->
      <el-table-column label="序号" type="index" width="100" align="center">
        <template slot-scope="scope">
          <span>{{(listQuery.current - 1) * listQuery.size + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="数据名称"
        align="center"
        prop="modeName"
        :show-overflow-tooltip="true"
        width="250"
      />

      <!--<el-table-column
        label="表描述"
        align="center"
        prop="tableComment"
        :show-overflow-tooltip="true"
        width="250"
      />-->
      <el-table-column
        label="数据分类"
        align="center"
        prop="modeType"
        :formatter="formatTableType"
        :show-overflow-tooltip="true"
        width="200"
      />
      <!--
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
      />-->
      <el-table-column
        label="视图类型"
        align="center"
        prop="viewType"
        :formatter="formatViewType"
        :show-overflow-tooltip="true"
        width="200"
      />
      <el-table-column label="更新时间" :sortable="true" align="center" prop="updateTime" width="200" />-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['metadata:table:view']"
          >BI视图</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['metadata:table:delete']"
          >取消收藏</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-download"
            @click="handleSubcribe(scope.row)"
            v-hasPermi="['metadata:table:export']"
          >下载</el-button>
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
    <!-- 预览界面 -->
    <el-dialog :title="preview.title" :visible.sync="preview.open" width="80%" top="5vh" append-to-body>

    </el-dialog>
  </div>
</template>

<script>
    import * as dsQueryApi from '@/api/datasync/metadataquery'
    import { downLoadZip } from "@/utils/zipdownload";
    import { list as jdbcDsList } from '@/api/datasource/datasource'
    export default {
        name: "subscribeForm",
        data() {
            return {
                // 遮罩层
                loading: true,
                // 唯一标识符
                uniqueId: "",
                // 选中数组
                ids: [],
                rDsList: [],
                // 选中表数组
                typeOptions: [],
                cycleTypeOptions:[],
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
                    tableType: undefined,
                    viewType:undefined
                },
                jdbcDsQuery:{
                    current: 1,
                    size: 200,
                    ascs: 'id',
                    datasource_group: '1',
                },
                // 预览参数
                preview: {
                    open: false,
                    title: "代码预览",
                    data: {},
                    activeName: "domain.java"
                }
            };
        },
        created() {
            this.getList();
            this.getJdbcDs();
            this.getDicts("sys_table_type").then(response => {
                this.typeOptions = response.data;
            });
            if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
            {
                this.rDsChange(this.listQuery.datasourceId);
            }
            this.getDicts("sys_table_cycle_list").then(response => {
                this.cycleTypeOptions = response.data;
            });
            this.getDicts("sys_dataview_type").then(response => {
                this.viewTypeOptions = response.data;
            });
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
            formatViewType(row, column, cellValue) {
                var ret = ''  //你想在页面展示的值
                this.viewTypeOptions.find((item) => {
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
                dsQueryApi.listSubcribes(this.listQuery).then(response => {
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
            /** 生成代码操作 */
            handleGenTable(row) {
                const tableNames = row.tableName || this.tableNames;
                if (tableNames === "") {
                    this.msgError("请选择要生成的数据");
                    return;
                }
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
                const modeId = row.id;
                const modeName = row.modeName;
                this.$confirm('是否确认取消收藏表:"' + modeName + '"?', "警告", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(function() {
                    return dsQueryApi.deleteSubcribe({modeId});
                }).then(() => {
                    this.getList();
                    this.msgSuccess("取消成功");
                })
            }
        }
    };
</script>
