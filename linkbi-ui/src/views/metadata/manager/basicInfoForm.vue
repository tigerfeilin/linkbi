<template>
  <el-form ref="basicInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item label="表名称" prop="tableName">
          <el-input :disabled="true" placeholder="请输入表名称" v-model="info.tableName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="中文名" prop="tableName">
          <el-input placeholder="请输入中文名称" v-model="info.tableChName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="表描述" prop="tableComment">
          <el-input placeholder="请输入描述" v-model="info.tableComment" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="表分类" prop="className">
          <el-select v-model="info.tableType" @change="rTypeChange" placeholder="表分类" clearable size="small">
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
          <el-select v-model="info.tableCycle" placeholder="生命周期" clearable size="small">
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
</template>
<script>
export default {
  name: "BasicInfoForm",
  props: {
    info: {
      type: Object,
      default: null
    },
  },
  data() {
    return {
      cycleTypeOptions:[],
      typeOptions:[],
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
      rTypeChange(e) {
          console.log(e);
      }
  }
};
</script>
