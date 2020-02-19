<template>
    <el-container id="api-container" style="min-width: 1250px">
        <el-header height="45px" style="padding:10px;text-align: left;line-height: 25px;">
            <template v-if="$route.path==='/api/edit'">
                <el-button type="success" size="mini" @click="save">Save</el-button>
                <el-button size="mini" @click="view">Cancel</el-button>
            </template>
            <template v-if="$route.path==='/api/detail'">
                <el-link class="a-link" @click="test">Test</el-link>
                <el-link class="a-link" @click="edit" v-if="hasAuth">Edit</el-link>
                <el-link class="a-link" v-if="hasAuth">Copy</el-link>
                <el-link class="a-link" v-if="hasAuth">Delete</el-link>
            </template>
            <template v-if="$route.path==='/api/test'">
                <el-link class="a-link" @click="edit" v-if="hasAuth">Edit</el-link>
                <el-link class="a-link" @click="view">View</el-link>
            </template>
            <el-select v-model="selectedEnvName" filterable placeholder="choose environment" clearable
                       @change="selectEnv" style="float: right;margin-right: 30px" size="mini">
                <el-option v-for="item in envConfigList" :key="item.id" :value="item">
                    <el-tooltip :content="item.frontUri" placement="left">
                        <span style="width: 100%">{{item.name}}</span>
                    </el-tooltip>
                </el-option>
            </el-select>
        </el-header>
        <el-main>
            <router-view ref="api-sub-router"/>
        </el-main>
    </el-container>
</template>

<script type="text/ecmascript-6">
    import {CONSTANT} from "../../common/js/constant";
    import {UTILS} from "../../common/js/utils";

    export default {
        name: 'index',
        data() {
            return {
                selectedProjectUserType: this.$store.getters.selectedProjectUserType,
                projectId: this.$store.getters.selectedProjectId,
                envConfigList: [],
                selectedEnv: undefined,
                selectedEnvName: '',
            }
        },
        computed: {
            hasAuth() {
                return this.selectedProjectUserType !== CONSTANT.AUTH_ROLE.READ;
            }
        },
        methods: {
            init() {
                this.$axios.post(CONSTANT.REQUEST_URL.API_ENV_FIND_LIST, {projectId: this.projectId}).then(resp => {
                    if (UTILS.checkResp(resp)) {
                        this.envConfigList = resp.data.data;
                    }
                });
            },
            selectEnv(env) {
                this.selectedEnv = env;
                this.selectedEnvName = env.name;
                let $ref = this.$refs['api-sub-router'];
                if ($ref.flushEnv) {
                    $ref.flushEnv(env);
                }
            },
            save() {
                this.$refs['api-sub-router'].save();
            },
            edit() {
                this.$router.push({
                    path: '/api/edit',
                    query: {id: this.$route.query.id}
                });
            },
            test() {
                this.$router.push({
                    path: '/api/test',
                    query: {
                        id: this.$route.query.id,
                        selectedEnv: this.selectedEnv,
                    }
                });
            },
            view() {
                this.$router.push({
                    path: '/api/detail',
                    query: {id: this.$route.query.id}
                });
            }
        },
        created() {
            this.init();
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    #api-container
        border 1px solid #d9d9d9

        .el-header
            background-color #fff
            color #333
            text-align center
            line-height 60px

        .el-main
            background-color #fff
            color #333
            text-align center
            line-height 160px

        .a-link
            margin 5px
</style>
