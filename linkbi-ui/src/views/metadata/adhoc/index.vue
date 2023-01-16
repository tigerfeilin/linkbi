<template>
  <div class="app-container">
    <el-form v-loading = "loading" ref="queryForm"  :inline="true" :model="listQuery" >
      <el-form-item label="查询方式" prop="searchType">
        <el-select v-model="listQuery.searchType" filterable  @change="rSearchTypeChange">
          <el-option
            v-for="dict in rSearhTypeList"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="listQuery.searchType === '1'" label="数据源" prop="datasourceId">
        <el-select v-model="listQuery.datasourceId" filterable  @change="rDsChange">
          <el-option
            v-for="item in rDsList"
            :key="item.id"
            :label="item.datasourceName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="listQuery.searchType === '1' && (dataSource==='postgresql' || dataSource==='oracle' ||dataSource==='sqlserver') " label="Schema" prop="tableSchema">
        <el-select v-model="listQuery.tableSchema" allow-create default-first-option filterable style="width: 300px" @change="schemaChange">
          <el-option
            v-for="item in schemaList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-show="listQuery.searchType === '1'" label="表名" prop="tableName">
        <el-select v-model="listQuery.tableName" allow-create default-first-option filterable style="width: 300px" >
          <el-option v-for="item in rTbList" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="SQL语句">
        <el-row>
          <el-col >
            <el-input v-model="listQuery.querySql" :autosize="{ minRows: 3, maxRows: 5}" type="textarea" placeholder="sql查询" style="width: 800px" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="fetchData">查询</el-button>
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="warning" icon="el-icon-medal-1" size="mini" @click="handleImportMarket">归集</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" v-show="total>0" style="width: 100%" :data="tableData">
      <template v-for="(item,index) in tableHead">
        <el-table-column :prop="item.col_name" :label="item.col_comment" :key="index" :sortable="true" :show-overflow-tooltip="true"></el-table-column>
      </template>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.current" :limit.sync="listQuery.size" @pagination="fetchData" />

    <el-dialog title="数据归集" :visible.sync="open" width="40%" top="5vh" append-to-body>
      <el-form ref="dataForm" :rules="rules" :model="marketQuery" label-position="left" label-width="100px">
        <el-form-item v-if = "false" label="数据源" prop="modeName">
          <el-input v-model="marketQuery.datasourceId" placeholder="数据源" style="width: 40%" />
        </el-form-item>
        <el-form-item label="数据名称" prop="modeName">
          <el-input v-model="marketQuery.modeName" placeholder="模型名称" style="width: 40%" />
        </el-form-item>
        <el-form-item label="数据类型" prop="modeType">
          <el-select v-model="marketQuery.modeType" filterable size="small" >
            <el-option
              v-for="dict in typeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
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
  </div>
</template>

<script>
    import * as dsQueryApi from '@/api/datasync/metadataquery'
    import { list as jdbcDsList } from '@/api/datasource/datasource'

    export default {
        name: 'Adhoc',
        data() {
            return {
                jdbcDsQuery: {
                    current: 1,
                    size: 200,
                    ascs: 'id',
                    //datasource_group: '1'
                },
                rSearhTypeList: [],
                rDsList: [],
                rTbList: [],
                schemaList: [],
                rColumnList: [],
                loading: false,
                open:false,
                active: 1,
                customFields: '',
                customType: '',
                customValue: '',
                dataSource: '',
                typeOptions: [],
                queryTypeOptions:[],
                viewTypeOptions:[],
                total: 0,
                listQuery: {
                    current: 1,
                    size: 10,
                    searchType:'1',
                    datasourceId: undefined,
                    tableName: '',
                    querySql: '',
                    tableSchema: ''
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
                tableHead:[],
                tableData: [],
                rules: {
                    modeName: [{ required: true, message: 'this is required', trigger: 'blur' }],
                    modeType: [{ required: true, message: 'this is required', trigger: 'blur' }],
                    viewType: [{ required: true, message: 'this is required', trigger: 'blur' }],
                    datasourceId: [{ required: true, message: 'this is required', trigger: 'blur' }]
                },
            }
        },
        watch: {
            'listQuery.datasourceId': function() {
                if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
                {
                    this.rDsChange(this.listQuery.datasourceId);
                }
                if (this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                    this.getSchema()
                } else {
                    this.getTables('rdbmsReader')
                }
            }
        },
        created() {
            this.getJdbcDs();
            if(this.listQuery.datasourceId !== 0 && this.listQuery.datasourceId !=='' && this.listQuery.datasourceId !== undefined)
            {
                this.rDsChange(this.listQuery.datasourceId);
            }
            this.getDicts("sys_datasource_group").then(response => {
                this.rSearhTypeList = response.data;
            });
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
        },
        methods: {
            // 获取可用数据源
            fetchData()
            {
                if(this.listQuery.searchType === 'normal' && this.listQuery.datasourceId === undefined)
                {
                    this.msgError("请选择数据源")
                    return
                }
                this.loading = true;
                dsQueryApi.adhoc(this.listQuery).then(response => {
                    //console.log(response);
                    this.tableHead = response.cols
                    this.tableData = response.rows
                    this.total = response.total
                    this.loading = false
                }).catch((e) =>
                {
                    this.loading = false
                    this.msgError(e)
                })
            },
            resetQuery()
            {
                this.listQuery.querySql=''
                this.resetForm("queryForm")
            },
            handleImportMarket() {
                const obj = {
                    datasourceId: this.listQuery.datasourceId,
                    schema:this.listQuery.tableSchema,
                    tablename:this.listQuery.tableName
                }
                this.resetForm("queryForm")
                this.marketQuery.datasourceId = obj.datasourceId
                this.marketQuery.schemaName = obj.schema
                this.marketQuery.modeName = obj.tablename
                if(this.listQuery.querySql === '')
                {
                    this.loading = true
                    dsQueryApi.queryModelSql(obj).then(sql => {
                        this.marketQuery.querySql = sql.data
                        this.loading = false
                        this.open = true
                    }).catch(e =>{
                        this.msgError(e);
                        this.loading = false
                    })
                }
                else{
                    this.marketQuery.querySql = this.listQuery.querySql
                    this.open = true
                }
            },
            /** 数据归集操作 */
            importMarket() {
                this.loading = true;
                this.$refs['dataForm'].validate((valid) => {
                    if (valid && this.marketQuery.datasourceId !== undefined) {
                        //console.log(this.marketQuery)
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
                    }
                    else{
                        this.msgError("数据格式错误");
                        this.loading = false
                    }
                })

            },
            getJdbcDs() {
                this.loading = true
                jdbcDsList(this.jdbcDsQuery).then(response => {
                    const { records } = response.data
                    this.rDsList = records
                    this.loading = false
                }).catch( e => {
                  this.loading = false
                  this.msgError(e);
                })
            },
            // 获取表名
            getTables(type) {
                if (type === 'rdbmsReader') {
                    let obj = {}
                    if (this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                        obj = {
                            datasourceId: this.listQuery.datasourceId,
                            tableSchema: this.listQuery.tableSchema
                        }
                    } else {
                        obj = {
                            datasourceId: this.listQuery.datasourceId
                        }
                    }
                    // 组装
                    this.loading = true;
                    dsQueryApi.getTables(obj).then(response => {
                        if (response) {
                            this.rTbList = response.data
                            this.loading = false
                        }
                    }).catch( e => {
                      this.loading = false
                      this.msgError(e);
                    })
                }
            },
            getSchema() {
                const obj = {
                    datasourceId: this.listQuery.datasourceId
                }
                this.loading = true
                dsQueryApi.getTableSchema(obj).then(response => {
                    this.schemaList = response.data
                    this.loading = false
                }).catch( e => {
                  this.loading = false
                  this.msgError(e);
                })
            },
            // schema 切换
            schemaChange(e) {
                this.listQuery.tableSchema = e
                // 获取可用表
                this.getTables('rdbmsReader')
            },
            rSearchTypeChange(e)
            {
                this.searchType = e
            },
            // reader 数据源切换
            rDsChange(e) {
                // 清空
                this.listQuery.tableName = ''
                this.listQuery.tableSchema = ''
                this.listQuery.datasourceId = e
                this.loading = true
                if(this.rDsList.length === 0)
                {
                    jdbcDsList(this.jdbcDsQuery).then(response => {
                        const { records } = response.data
                        this.rDsList = records
                        this.rDsList.find((item) => {
                            if (item.id === e) {
                                this.dataSource = item.datasource
                                if (this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                                    this.getSchema()
                                } else {
                                    this.getTables('rdbmsReader')
                                }
                            }
                        })
                        this.loading = false
                    }).catch( e => {
                      this.loading = false
                      this.msgError(e);
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
                if (this.dataSource === 'postgresql' || this.dataSource === 'oracle' || this.dataSource === 'sqlserver') {
                    this.getSchema()
                } else {
                    this.getTables('rdbmsReader')
                }
            },
            getData() {
                return this.listQuery
            }
        }
    }
</script>
