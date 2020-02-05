console.log('vpi plugin');
setTimeout(() => {
    let app = new Vue({
        el: '#app-hidden',
        // el: '#vpiPlugin',
        data: {
            message: 'Hello Vue!'
        },
        mounted() {
            console.log('author by xyyxhcj@qq.com');
            console.log(1);
            document.getElementById('testButton').addEventListener('click', function () {
                console.log(document.getElementById('test-params').innerHTML);
            });
            console.log('add listener finish');
            // vm.$destroy()
        },
        beforeDestroy() {
            this.$destroy();
        }
    });
}, 1000);
