<template>
    <div class="title">
        <div class="text">{{text}}</div>
        <div class="line"></div>
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        name: 'lineText',
        props: {
            text: {
                type: String
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus" scoped>
    .title
        display: flex
        width: 100%
        margin: 20px auto 16px

        .line
            flex: 1
            position: relative
            top: -6px
            border-bottom: 1px solid #B3C0D1

        .text
            padding: 0 12px
            font-weight: 700
            font-size: 14px
</style>
