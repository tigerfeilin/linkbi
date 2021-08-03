<template>
  <div class="app-container">
  <el-card v-loading="loading" >
    <el-tabs v-model="activeName">
      <el-tab-pane label="我的收藏" name="basic">
        <subscribe-form ref="subscribeInfo" :model="queryParas.info" />
      </el-tab-pane>
      <el-tab-pane label="数据集市" name="column">
        <market-form ref="marketInfo" :model="queryParas.info" />
      </el-tab-pane>
    </el-tabs>
    <!--
    <el-form label-width="100px">
      <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
        <el-button type="primary" @click="submitForm()" >提交</el-button>
        <el-button @click="close()">返回</el-button>
      </el-form-item>
    </el-form>-->
  </el-card>
  </div>
</template>
<script>
    import * as dsQueryApi from '@/api/datasync/metadataquery'
    import subscribeForm from "./subscribeForm";
    import marketForm from "./marketForm";
    import dataExportForm from "./dataExportForm";

    export default {
        name: "market",
        components: {
            subscribeForm,
            marketForm,
            dataExportForm
        },
        data() {
            return {
                // 选中选项卡的 name
                activeName: "basic",
                loading: false,
                // 表格的高度
                tableHeight: document.documentElement.scrollHeight - 245 + "px",
                // 字典信息
                dictOptions: [],
                // 表详细信息
                cycleTypeOptions:[],
                typeOptions:[],
                queryParas:{
                    //columns: [],
                    info: undefined,
                },
                rules: {
                    tableName: [
                        { required: true, message: "请输入表名称", trigger: "blur" }
                    ],
                    tableComment: [
                        { required: true, message: "请输入表描述", trigger: "blur" }
                    ],
                    className: [
                        { required: true, message: "请输入实体类名称", trigger: "blur" }
                    ]
                }
            };
        },
        created() {
            this.getDicts("sys_table_cycle_list").then(response => {
                this.cycleTypeOptions = response.data;
            });
            this.getDicts("sys_table_type").then(response => {
                this.typeOptions = response.data;
            });
        },
        methods: {
            /** 提交按钮 */
            submitForm() {
                this.loading = true;
                dsQueryApi.editTable(this.queryParas.info).then(res => {
                    this.msgSuccess('更新成功');
                    this.loading = false;
                }).catch(e=>{
                    this.msgError(e);
                    this.loading = false;
                });
            },
            /** 关闭按钮 */
            close() {
                this.$store.dispatch("tagsView/delView", this.$route);
                this.$router.push({ path: "/metadata/manager", query: { t: Date.now()}})
            },
            formatBoolean(row, column, cellValue) {
                var ret = ''  //你想在页面展示的值
                if (cellValue) {
                    ret = "是"  //根据自己的需求设定
                } else {
                    ret = "否"
                }
                return ret;
            }
        },

        /*
      mounted() {
        const el = this.$refs.dragTable.$el.querySelectorAll(".el-table__body-wrapper > table > tbody")[0];
        const sortable = Sortable.create(el, {
          handle: ".allowDrag",
          onEnd: evt => {
            const targetRow = this.columns.splice(evt.oldIndex, 1)[0];
            this.columns.splice(evt.newIndex, 0, targetRow);
            for (let index in this.columns) {
              this.columns[index].sort = parseInt(index) + 1;
            }
          }
        });
      }
         */
    };
</script>
