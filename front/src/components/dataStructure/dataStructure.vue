<template>
  <div class="data-structure-common">
    <el-table :data="showList" style="width: 100%" :row-style="rowStyle" border :ref="config.refPre+'table'">
      <el-table-column type="index" width="40"/>
      <el-table-column type="selection" :width="config.test?25:1"/>
      <el-table-column label="paramKey" width="280" ref="param-key-container">
        <template slot-scope="scope">
                    <span :style="{padding:countKeyPadding(scope.row)}">
                        <template v-if="scope.row.subList.length>0">
                            <i v-if="scope.row.showSub!==false" class="el-icon-remove-outline"
                               style="padding:5px 6px 5px 0" @click="hiddenSub(scope.row)"/>
                            <i v-if="scope.row.showSub===false" class="el-icon-circle-plus-outline"
                               style="padding:5px 6px 5px 0" @click="showSub(scope.row)"/>
                        </template>
                        <span style="border-left:1px solid #d9d9d9;padding:10px 2px" :key="index"
                              v-for="(item,index) in Array(scope.row.level)">
                            <i class="el-icon-arrow-right"/>
                        </span>
                    </span>
          <el-input v-model.trim="scope.row.paramKey" @input="paramKeyChange(scope.$index,scope.row)"
                    :placeholder="scope.row.paramKeyIsEmpty?'enter paramKey':''"
                    :style="{width:countKeyInputWidth(scope.row)}" size="mini"
                    :ref="config.refPre+'paramKey'+scope.$index"
                    @keyup.down.native="focusMoveDown(scope.$index,'paramKey')"
                    @keyup.up.native="focusMoveUp(scope.$index,'paramKey')"
                    v-if="!config.onlyRead&&!scope.row.reference"/>
          <template v-else>{{ scope.row.paramKey }}</template>
        </template>
      </el-table-column>
      <el-table-column label="paramType" width="110">
        <template slot-scope="scope">
          <el-select :value="scope.row.paramType+''" filterable size="mini"
                     @change="(selectedValue)=>scope.row.paramType=selectedValue"
                     v-if="!config.onlyRead&&!scope.row.reference">
            <el-option v-for="key in Object.keys(CONSTANT.PARAM_TYPE_STR)"
                       :key="key" :label="CONSTANT.PARAM_TYPE_STR[key]" :value="key"/>
          </el-select>
          <span style="margin-left:15px" v-else>{{ CONSTANT.PARAM_TYPE_STR[scope.row.paramType] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="requireType" width="125" v-if="!config.test">
        <template slot-scope="scope">
          <el-select :value="scope.row.requireType+''" size="mini"
                     @change="(selectedValue)=>scope.row.requireType=selectedValue"
                     v-if="!config.onlyRead&&!scope.row.reference">
            <el-option v-for="key in Object.keys(CONSTANT.REQUIRED_TYPE_STR)"
                       :key="key" :label="CONSTANT.REQUIRED_TYPE_STR[key]" :value="key"/>
          </el-select>
          <!-- 参数为可选时不显示必填类型 -->
          <span style="margin-left:15px"
                v-else>{{ scope.row.requireType === 1 ? '' : CONSTANT.REQUIRED_TYPE_STR[scope.row.requireType] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="paramDesc" width="180">
        <template slot-scope="scope">
          <el-input v-model.trim="scope.row.paramDesc" size="mini"
                    :ref="config.refPre+'paramDesc'+scope.$index"
                    @keyup.down.native="focusMoveDown(scope.$index,'paramDesc')"
                    @keyup.up.native="focusMoveUp(scope.$index,'paramDesc')"
                    v-if="!config.onlyRead&&!config.test&&!scope.row.reference"/>
          <span style="margin-left:15px" v-else>{{ scope.row.paramDesc }}</span>
        </template>
      </el-table-column>
      <el-table-column label="value" width="180">
        <template slot-scope="scope">
          <el-input v-model.trim="scope.row.value" size="mini"
                    :ref="config.refPre+'value'+scope.$index"
                    @keyup.down.native="focusMoveDown(scope.$index,'value')"
                    @keyup.up.native="focusMoveUp(scope.$index,'value')"
                    v-if="!config.onlyRead&&!scope.row.reference"/>
          <span style="margin-left:15px" v-else>{{ scope.row.value }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!config.onlyRead" width="350">
        <template slot="header">
          <template v-if="!entity.reference">
            <more-operate v-if="config.useReference" :info="{showDataStructure:showDataStructure}"/>
            <el-tooltip class="item" effect="dark" content="override" placement="right">
              <el-button size="mini" type="success" @click="showImportJsonDialog">Import Json</el-button>
            </el-tooltip>
            <el-button size="mini" @click="addField">Add Field</el-button>
          </template>
          <template v-else>
            <reference-info :info="entity"
                            @disconnectDataStructure="disconnectDataStructure"
                            @editDataStructure="editDataStructure"
                            @showDataStructure="showDataStructure"/>
          </template>
        </template>
        <template slot-scope="scope" v-if="!scope.row.reference">
          <template v-if="!scope.row.referenceStructureId">
            <more-operate v-if="config.useReference"
                          :info="{index:scope.$index,row:scope.row,showDataStructure:showDataStructure}"/>
            <el-tooltip class="item" effect="dark" content="additional" placement="right">
              <el-button size="mini" type="success" @click="showImportJsonDialog(scope.$index,scope.row)">
                Import Json
              </el-button>
            </el-tooltip>
            <el-button size="mini" @click="addSubField(scope.$index,scope.row)">Add Sub Field</el-button>
          </template>
          <template v-else>
            <reference-info :index="scope.$index" :info="scope.row"
                            @disconnectDataStructure="disconnectDataStructure"
                            @editDataStructure="editDataStructure"
                            @showDataStructure="showDataStructure"/>
          </template>
          <el-button size="mini" type="primary" class="el-icon-top" @click="moveUp(scope.row)"
                     :disabled="upIsDisabled(scope.$index,scope.row)"/>
          <el-button size="mini" type="primary" class="el-icon-bottom" @click="moveDown(scope.row)"
                     :disabled="downIsDisabled(scope.row)"/>
          <el-button style="position: absolute;right: 0" size="mini" type="danger"
                     @click="del(scope.$index,scope.row)">Delete
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <import-json-dialog :dialog="importJsonDialog" @flush="importJson"/>
    <select-data-structure-dialog :dialog="selectDataStructureDialog" @selectDataStructure="selectDataStructure"
                                  ref="selectDataStructureDialog"/>
  </div>
</template>

<script type="text/ecmascript-6">
import {CONSTANT} from "../../common/js/constant";
import ImportJsonDialog from "./importJsonDialog";
import {UTILS} from "../../common/js/utils";
import MoreOperate from "./components/moreOperate";
import SelectDataStructureDialog from "./selectDataStructureDialog";
import ReferenceInfo from "./components/referenceInfo";

export default {
  name: 'dataStructure',
  components: {ReferenceInfo, SelectDataStructureDialog, MoreOperate, ImportJsonDialog},
  props: {
    showList: {
      type: Array,
    },
    entity: {
      type: Object,
      default() {
        return {
          id: undefined,
          reference: false,
          // root tree
          dataList: [],
        };
      }
    },
    config: {
      type: Object,
      default() {
        return {
          refPre: '',
          onlyRead: false,
          test: false,
          useReference: false,
        }
      }
    }
  },
  data() {
    return {
      CONSTANT,
      importJsonDialog: {
        show: false,
        title: 'import json',
        parentLevel: -1,
        row: undefined,
        rowIndex: undefined,
      },
      selectDataStructureDialog: {
        show: false,
        title: 'select data structure',
        selectedIndex: undefined,
        selectedRow: undefined,
      },
    }
  },
  methods: {
    moveUp(row) {
      let arr;
      arr = row.parent ? row.parent.subList : this.entity.dataList;
      let curr = arr.indexOf(row);
      if (curr > 0) {
        arr.splice(curr - 1, 0, arr.splice(curr, 1)[0])
      }
      UTILS.fillShowList(this.entity.dataList, this.showList, false, false);
    },
    moveDown(row) {
      let arr;
      arr = row.parent ? row.parent.subList : this.entity.dataList;
      let curr = arr.indexOf(row);
      if (curr < arr.length - 1) {
        arr.splice(curr + 1, 0, arr.splice(curr, 1)[0])
      }
      UTILS.fillShowList(this.entity.dataList, this.showList, false, false);
    },
    upIsDisabled(index, row) {
      if (row.parent) {
        return row.parent.subList.indexOf(row) === 0;
      }
      return index === 0;
    },
    downIsDisabled(row) {
      if (!this.entity.dataList) {
        return;
      }
      if (row.parent) {
        return row.parent.subList.indexOf(row) === row.parent.subList.length - 1;
      }
      return this.entity.dataList.indexOf(row) === this.entity.dataList.length - 1;
    },
    signSelected() {
      let selectedList = this.$refs[this.config.refPre + 'table'].selection;
      this.showList.forEach(row => {
        row.selected = selectedList.indexOf(row) > -1;
      });
    },
    importJson(data) {
      if (!data) {
        return;
      }
      if (this.importJsonDialog.row === undefined) {
        this.entity.dataList.splice(0);
        data.forEach(item => this.entity.dataList.push(item));
      } else {
        if (this.importJsonDialog.row.subList !== undefined) {
          let dict = {};
          for (let i = 0; i < this.importJsonDialog.row.subList.length; i++) {
            dict[this.importJsonDialog.row.subList[i].paramKey] = i;
          }
          data.forEach(item => {
            let index = dict[item.paramKey];
            if (index !== undefined) {
              this.importJsonDialog.row.subList[index] = item;
            } else {
              this.importJsonDialog.row.subList.push(item);
            }
          });
        } else {
          this.importJsonDialog.row.subList = [];
        }
      }
      UTILS.fillShowList(this.entity.dataList, this.showList);
      this.$refs[this.config.refPre + 'table'].toggleAllSelection();
    },
    showImportJsonDialog(index, row) {
      this.importJsonDialog.row = row;
      if (row !== undefined) {
        this.importJsonDialog.rowIndex = index;
        this.importJsonDialog.parentLevel = row.level;
      } else {
        this.importJsonDialog.rowIndex = undefined;
        this.importJsonDialog.parentLevel = -1;
      }
      this.importJsonDialog.show = true;
    },
    focusMoveUp(index, refMid) {
      if (index !== 0) {
        this.$refs[this.config.refPre + refMid + (index - 1)].focus();
      }
    },
    focusMoveDown(index, refMid) {
      if (index !== this.showList.length - 1) {
        this.$refs[this.config.refPre + refMid + (index + 1)].focus();
      }
    },
    rowStyle({row}) {
      return {
        'display': row.show ? '' : 'none',
        'background-color': !this.config.onlyRead && row.reference ? '#f0f0f0' : ''
      };
    },
    countKeyPadding(row) {
      return row.level > 0 && row.subList.length === 0 ? '0 0 0 20px' : '0';
    },
    hiddenSub(row) {
      this.$set(row, 'showSub', false);
      let stack = row.subList.slice();
      while (stack.length > 0) {
        let pop = stack.shift();
        pop.show = false;
        if (pop.subList && pop.subList.length > 0) {
          pop.subList.forEach(item => stack.push(item));
        }
      }
    },
    showSub(row) {
      row.showSub = true;
      let stack = row.subList.slice();
      while (stack.length > 0) {
        let pop = stack.shift();
        pop.show = true;
        if (pop.showSub !== false && pop.subList && pop.subList.length > 0) {
          pop.subList.forEach(item => stack.push(item));
        }
      }
    },
    paramKeyChange(index, row) {
      if (!row.parent && this.entity.dataList[this.entity.dataList.length - 1] === row) {
        // add root
        this.addField();
      }
      this.$refs[this.config.refPre + 'table'].toggleRowSelection(row, true);
      row.paramKeyIsEmpty = row.paramKey === '' && (row.paramDesc !== '' || row.value !== '');
    },
    countKeyInputWidth(row) {
      let preWidth = row.level > 0 || row.subList.length > 0 ? 20 : 0;
      for (let i = 0; i < row.level; i++) {
        preWidth += 19;
      }
      return this.$refs['param-key-container'].width - 20 - preWidth + 'px';
    },
    addField() {
      UTILS.pushItemTemplate(this.entity.dataList, this.showList);
    },
    addSubField(index, row) {
      let item = JSON.parse(CONSTANT.ITEM_TEMPLATE);
      item.level = row.level + 1;
      item.parent = row;
      row.subList.splice(0, 0, item);
      this.showList.splice(index + 1, 0, item);
    },
    del(index, row) {
      let parent = row.parent;
      // remove from showList,with subTree
      // get same level next element
      if (parent) {
        let curr = row;
        while (parent.parent && parent.subList.indexOf(curr) === parent.subList.length - 1) {
          curr = parent;
          parent = parent.parent;
        }
        if (parent.subList.indexOf(curr) !== parent.subList.length - 1) {
          let nextIndex = this.showList.indexOf(parent.subList[parent.subList.indexOf(curr) + 1]);
          this.showList.splice(index, nextIndex - index);
        } else {
          let rootIndex = this.entity.dataList.indexOf(parent);
          if (rootIndex === this.entity.dataList.length - 1) {
            this.showList.splice(index, this.showList.length - index);
          } else {
            let nextIndex = this.showList.indexOf(this.entity.dataList[rootIndex + 1]);
            this.showList.splice(index, nextIndex - index);
          }

        }
      } else {
        let rootIndex = this.entity.dataList.indexOf(row);
        if (rootIndex === this.entity.dataList.length - 1) {
          this.showList.splice(index, this.showList.length - index);
        } else {
          let nextIndex = this.showList.indexOf(this.entity.dataList[rootIndex + 1]);
          this.showList.splice(index, nextIndex - index);
        }
      }
      // remove from tree
      if (parent) {
        parent.subList.splice(parent.subList.indexOf(row), 1);
      } else {
        this.entity.dataList.splice(this.entity.dataList.indexOf(row), 1);
      }
    },
    init() {
      if (this.showList.length === 0) {
        if (!this.entity.dataList) {
          this.entity.dataList = [];
        }
        for (let i = 0; i < CONSTANT.CONFIG.DEFAULT_DATA_LIST_SIZE; i++) {
          UTILS.pushItemTemplate(this.entity.dataList, this.showList);
        }
      }
      let $refTable = this.$refs[this.config.refPre + 'table'];
      this.showList.forEach(row => {
        if (row.paramKey !== '' && row.requireType === 0) {
          $refTable.toggleRowSelection(row);
        }
      });
    },
    showDataStructure(index, row) {
      this.selectDataStructureDialog.selectedIndex = index;
      this.selectDataStructureDialog.selectedRow = row;
      this.$refs['selectDataStructureDialog'].findPage();
      this.selectDataStructureDialog.show = true;
    },
    selectDataStructure(selectedDataStructure) {
      if (!selectedDataStructure) {
        return;
      }
      this.$axios.post(CONSTANT.REQUEST_URL.STRUCTURE_FIND_DETAIL, {id: selectedDataStructure.id}).then(resp => {
        if (UTILS.checkResp(resp)) {
          let dataList = resp.data.data.dataList;
          if (dataList.length === 0) {
            this.$message({
              message: 'data structure lose data: ' + resp.data.data.name,
              type: 'error'
            });
            return;
          }
          let addTemplate;
          if (this.selectDataStructureDialog.selectedIndex !== undefined) {
            // add to sub
            this.selectDataStructureDialog.selectedRow.subList = dataList;
            this.selectDataStructureDialog.selectedRow.referenceStructureId = selectedDataStructure.id;
            this.selectDataStructureDialog.selectedRow.referenceStructureName = selectedDataStructure.name;
            addTemplate = true;
          } else {
            // override all
            this.entity.dataList = dataList;
            this.entity.id = this.entity.referenceStructureId = selectedDataStructure.id;
            this.$delete(this.entity, 'referenceStructureName');
            this.$set(this.entity, 'referenceStructureName', selectedDataStructure.name);
            this.entity.reference = true;
            addTemplate = false;
          }
          UTILS.fillShowList(this.entity.dataList, this.showList, this.entity.reference, addTemplate);
          this.$refs[this.config.refPre + 'table'].toggleAllSelection();
        }
      });
    },
    editDataStructure(structureId) {
      this.$emit('editDataStructure', structureId);
    },
    disconnectDataStructure(info) {
      let stack = Array(info);
      while (stack.length > 0) {
        let pop = stack.pop();
        pop.id = '';
        pop.reference = false;
        pop.referenceStructureName = undefined;
        if (pop.structureId === pop.referenceStructureId) {
          pop.structureId = undefined;
        }
        pop.referenceStructureId = undefined;
        if (pop.dataList && pop.dataList.length > 0) {
          pop.dataList.forEach(item => stack.push(item));
        }
        if (pop.subList && pop.subList.length > 0) {
          pop.subList.forEach(item => stack.push(item));
        }
      }
      UTILS.fillShowList(this.entity.dataList, this.showList, this.entity.reference, false);
    }
  },
};
</script>

<style lang="stylus" rel="stylesheet/stylus">
.data-structure-common
  input
    &::-webkit-input-placeholder
      color #F56C6C

  .el-table td, .el-table th
    padding 0 0 0 3px

  .cell, div.cell
    padding 0 0 0 3px
    height 28px

  .el-button
    padding 4px 4px
    margin-top 1.5px

  .more-operate
    padding 10px
    cursor pointer
    color #409EFF
</style>
