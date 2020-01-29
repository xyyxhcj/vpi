<template>
    <el-form :model="form" label-position="left" label-width="50px" style="background-color: #f5f5f5;">
        <el-form-item label="path" size="mini">
            <el-input v-model="form.path" size="mini"/>
        </el-form-item>
        <el-form-item label="name" size="mini">
            <el-input v-model="form.name" size="mini"/>
        </el-form-item>
        <el-form-item label="status" size="mini">
            <el-input v-model="form.type" size="mini"/>
        </el-form-item>
    </el-form>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'edit',
        props: {
            form: {
                type: Object,
                default() {
                    return {
                        name: '',
                    }
                }
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
</style>
