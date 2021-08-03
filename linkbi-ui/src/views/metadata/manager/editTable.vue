<template>
  <el-card v-loading="loading" >
    <el-tabs v-model="activeName">
      <el-tab-pane label="基本信息" name="basic">
        <!--<basic-info-form ref="basicInfo" :info="info" />-->
        <el-form ref="basicInfoForm" :model="queryParas.info.metaData" :rules="rules" label-width="150px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="表名称" prop="tableName">
                <el-input :disabled="true" placeholder="请输入表名称" v-model="queryParas.info.metaData.tableName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="中文名" prop="tableName">
                <el-input placeholder="请输入中文名称" v-model="queryParas.info.metaData.tableChName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="表描述" prop="tableComment">
                <el-input placeholder="请输入描述" v-model="queryParas.info.metaData.tableComment" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="表分类" prop="className">
                <el-select v-model="queryParas.info.metaData.tableType" placeholder="表分类" clearable size="small">
                  <el-option
                    v-for="dict in typeOptions"
                    :key="dict.dictValue"
                    :label="dict.dictLabel"
                    :value="dict.dictValue"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="生命周期" prop="className">
                <el-select v-model="queryParas.info.metaData.tableCycle" placeholder="生命周期" clearable size="small">
                  <el-option
                    v-for="dict in cycleTypeOptions"
                    :key="dict.dictValue"
                    :label="dict.dictLabel"
                    :value="dict.dictValue"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="字段信息" name="column">
        <el-table ref="dragTable" :data="queryParas.info.columnList" row-key="columnId" :max-height="tableHeight">
          <el-table-column label="序号" type="index" min-width="5%" class-name="allowDrag" />
          <el-table-column
            label="字段列名"
            prop="fieldName"
            min-width="10%"
            :show-overflow-tooltip="true"
          />
          <el-table-column label="字段描述" min-width="10%">
            <template slot-scope="scope">
              <el-input v-model="scope.row.remarks"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="字段类型"
            prop="fieldTypeName"
            min-width="10%"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="字段长度"
            prop="displaySize"
            min-width="10%"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="主键自增"
            prop="isAutoIncrement"
            min-width="10%"
            :formatter="formatBoolean"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="非空"
            prop="isNullable"
            min-width="10%"
            :formatter="formatBoolean"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="脱敏"
            prop="isEncrypt"
            min-width="10%"
            :formatter="formatBoolean"
            :show-overflow-tooltip="true"
          />
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-form label-width="100px">
      <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
        <el-button type="primary" @click="submitForm()" >提交</el-button>
        <el-button @click="close()">返回</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
import * as dsQueryApi from '@/api/datasync/metadataquery'
import basicInfoForm from "./basicInfoForm";
import dataExportForm from "./dataExportForm";

export default {
  name: "GenEdit",
  components: {
    basicInfoForm,
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
    const tableId = this.$route.params && this.$route.params.tableId;
    if (tableId) {
      // 获取表详细信息
        dsQueryApi.getTabInfo({tableId}).then(res => {
        //this.columns = res.data;
        this.queryParas.info = res.data;
      });
    }
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
