window.onload = function () {
    Vue.component('version', {
        data() {
            return {
                version: '',
                message: '',
                compilationDate: ''
            }
        },
        template: '<div id="version" class="right tooltipped hide-on-med-and-down">{{version}}</div>',

        mounted() {
            var vm = this;
            axios.get('/version')
                .then(function (response) {
                    vm.version = response.data.version;
                    vm.message = response.data.message;

                    $('#version').tooltip({
                        delay: 50,
                        tooltip: response.data.message + '<br/>' + response.data.compilationDate,
                        position: 'top',
                        html: true
                    });
                })
                .catch(function () {
                    this.errors.push(e)
                })
        }
    });

    Vue.component('category-tree', {
        template:
        '<div id="categoryPicker" @mouseleave="mouseLeave" v-bind:class="{ \'category-picker-error\': isError }" class="category-picker">' +
            '<div v-if="pickedCategory == null">' +
                '<div class="row">' +
                    '<div v-if="parentNames.length > 0" class="br">' +
                        '<a v-for="parentName in parentNames" class="breadcrumb-category breadcrumb">{{ parentName }}</a>' +
                    '   <a v-if="showReturn" class="category-previous right" @click="goToPrevious">Powrót</a>' +
                    '</div>' +
                    '<hr v-if="parentNames.length > 0">' +
                    '<div class="col offset-s5">' +
                        '<div v-bind:class="{ \'preloader-wrapper\': true, small: true, active: true, hide: !isLoading, \'category-picker-preloader\': true}">' +
                            '<div class="spinner-layer spinner-yellow-only">' +
                                '<div class="circle-clipper left">' +
                                    '<div class="circle"></div>' +
                                '</div>' +
                                '<div class="gap-patch">' +
                                    '<div class="circle"></div>' +
                                '</div>' +
                                '<div class="circle-clipper right">' +
                                    '<div class="circle"></div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<ul id="category-list" v-bind:class="{ \'category-picker-list\': true, hide: isLoading }">' +
                    '<li v-for="category in categories">' +
                        '<a class="category" @click="itemClick(category)">{{ category.name }}</a>' +
                    '</li>' +
                '</ul>' +
            '</div>' +
            '<div class="picked-category" v-if="pickedCategory != null">' +
                '<div class="row">' +
                    '<div> Wybrana kategoria: {{ pickedCategory.name }}' +
                        '<a v-if="showChange" class="category right change-choice" @click="changeChoice">Zmień</a>' +
                        '<input type="hidden" name="categoryId" :value="pickedCategory.id">' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>',

        mounted() {
            var vm = this;
            vm.getTop();
        },

        data() {
            return {
                categories: [],
                previousId: 0,
                showReturn: false,
                showChange: false,
                isLoading: false,
                pickedCategory: null,
                parentNames: [],
                isError:false
            };
        },

        methods: {
            mouseLeave: function () {
              vm = this;
              if(vm.pickedCategory === null) {
                  vm.isError = true;
              }
            },
            changeChoice: function() {
                vm = this;
                vm.showChange = false;
                vm.pickedCategory = null;
                vm.getTop();
            },
            itemClick: function(category) {
                var vm = this;
                if(category.leaf) {
                    vm.pickedCategory = category;
                    vm.showChange = true;
                    vm.isError = false;
                } else {
                    this.getChildren(category.id);
                }
            },
            getChildren: function (id) {
                var vm = this;
                vm.isLoading = true;
                axios.get('/category-rest/get-children/' + id)
                    .then(function (response) {
                        vm.categories = response.data;
                        vm.previousId = vm.categories[0].parentOfParentId !== null ? vm.categories[0].parentOfParentId : 0;
                        vm.showReturn = true;
                        vm.isLoading = false;
                        vm.addParentNameToArray();
                    })
                    .catch(function () {
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            addParentNameToArray: function() {
                var vm = this;
                vm.parentNames.push(vm.categories[0].parentName !== null ? vm.categories[0].parentName : null);
            },
            goToPrevious: function () {
                var vm = this;
                vm.parentNames = [];
                vm.breadCrumbs = null;
                if (vm.previousId === 0) {
                    vm.getTop();
                    vm.showReturn = false;
                } else {
                    vm.getChildren(vm.previousId, false);
                }
            },
            getTop: function () {
                var vm = this;
                vm.isLoading = true;
                axios.get('/category-rest/get-all-top')
                    .then(function (response) {
                        vm.parentNames = [];
                        vm.categories = response.data;
                        vm.isLoading = false;
                    })
                    .catch(function () {
                        this.errors.push(e);
                        console.log(e);
                    })
            }

        }
    });

    var app = new Vue({
        el: '#app',
    });
}