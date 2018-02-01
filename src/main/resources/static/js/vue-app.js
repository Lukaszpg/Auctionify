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

    Vue.component('gallery', {
        data() {
            return {
                photos: [],
                currentPhoto: ""
            }
        },
        template:
        '<div>' +
            '<div class="row">' +
                '<div class="col s8 offset-s2">' +
                    '<div class="gallery-border row">' +
                        '<div class="col s8 offset-s2">' +
                            '<img @click="openFullscreen()" class="main-gallery-image responsive-img" v-bind:src="currentPhoto.srcPath">' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>' +
            '<div class="row">' +
                '<div v-for="photo in photos" class="col s4">' +
                    '<div class="row">' +
                        '<div class="col s12">' +
                            '<img @click="setMainPhoto(photo)" class="gallery-thumbnail" v-bind:src="photo.srcPath">' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>' +
            '<div id="galleryFullscreenModal" class="modal">' +
                '<div class="modal-content">' +
                    '<div class="row">' +
                        '<div class="col s8 offset-s2">' +
                            '<img class="gallery-fullscreen-img" v-bind:src="currentPhoto.srcPath">' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>',

        methods: {
            prepareSrcAttributes: function() {
                var vm = this;
                var photosWithSrcPath = [];

                for(var i = 0; i < vm.photos.length; i++) {
                    var photo = vm.photos[i];
                    photo.srcPath = "data:image/png;base64," + photo.image;
                    photosWithSrcPath.push(photo);
                }

                vm.photos = photosWithSrcPath;
            },

            setMainPhoto: function(photo) {
                var vm = this;
                vm.currentPhoto = photo;
            },

            openFullscreen: function() {
                $('.modal').modal();
                $('#galleryFullscreenModal').modal('open');
            }
        },

        mounted() {
            var vm = this;
            var id = $("#auctionId").val();
            axios.get('/photo/get-all/' + id)
                .then(function (response) {
                    vm.photos = response.data;
                    vm.prepareSrcAttributes();
                    vm.currentPhoto = vm.photos[0];
                })
                .catch(function () {
                    this.errors.push(e)
                })
        }
    });

    Vue.component('auction-observe', {
        template:
        '<a v-if="!isUserObserving" @click="observe" class="right observe-button waves-effect waves-teal btn-flat">' +
        '<i class="material-icons right">star_border</i>Obserwuj</a>' +
        '<a v-else-if="isUserObserving" @click="unobserve" class="right observe-button waves-effect waves-teal btn-flat">' +
        '<i class="material-icons right">star</i>Obserwujesz</a>',

        data() {
            return {
                isUserObserving: false,
                auctionId: null
            }
        },

        mounted() {
            var vm = this;
            vm.getAuctionId();
            vm.checkIsObserving();
        },

        methods: {
            checkIsObserving: function() {
                var vm = this;
                var id = vm.auctionId;
                axios.get('/auction-rest/is-observing/' + id)
                    .then(function (response) {
                        vm.isUserObserving = response.data;
                    })
                    .catch(function () {
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            observe: function() {
                var vm = this;
                var id = vm.auctionId;
                axios.get('/auction-rest/observe/' + id)
                    .then(function (response) {
                        if(response.data) {
                            vm.isUserObserving = true;
                            Materialize.toast('Pomyślnie dodano aukcję do obserwowanych.', 3000, 'toast-success')
                        } else {
                            Materialize.toast('Nie możesz obserwować własnej aukcji.', 3000, 'toast-error')
                        }
                    })
                    .catch(function () {
                        Materialize.toast('Wystąpił błąd podczas dodawania aukcji do obserwowanych.', 3000, 'toast-error');
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            unobserve: function() {
                var vm = this;
                var id = vm.auctionId;
                axios.get('/auction-rest/unobserve/' + id)
                    .then(function (response) {
                        if(response.data) {
                            vm.isUserObserving = false;
                            Materialize.toast('Aukcja usunięta z obserwowanych.', 3000, 'toast-success')
                        }
                    })
                    .catch(function () {
                        Materialize.toast('Nie udało się usunąć aukcji z obserwowanych.', 3000, 'toast-error');
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            getAuctionId: function() {
                var vm = this;
                var slashIndex = window.location.href.lastIndexOf('/');
                var result= window.location.href.substring(slashIndex  + 1);
                vm.auctionId = result;
            }
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
                $(".category-picker-error-message").addClass("hide");
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

    Vue.component('search', {
        data() {
            return {
                searchString: "",
                categoryId: "",
                auctions: [],
                categories: [],
                previousCategoryId: 0,
                isLoading: false,
                showReturn: false
            }
        },
        template:
            '<div>' +
                '<div class="row">' +
                    '<div class="col s2">' +
                        '<div class="row"></div>' +
                        '<div class="row"></div>' +
                        '<div class="row"></div>' +
                        '<div class="row"></div>' +
                        '<div class="row"></div>' +
                            '<h5>Podkategorie<i @click="previous" v-if="showReturn" class="filter-return-icon material-icons">undo</i></h5>' +
                            '<ul class="collection">' +
                                '<a @click="getChildren(category.id)" v-for="category in categories" class="collection-item">' +
                                    '<span class="title">{{ category.name }}</span>' +
                                    '<br/><label>{{ category.itemsAmount }} przedmiotów</label>' +
                                '</a>' +
                            '</ul>' +
                    '</div>' +
                    '<div v-if="auctions.length > 0" class="col s10">' +
                        '<h2>Wyniki wyszukiwania</h2>' +
                        '<div v-for="auction in auctions" class="single-auction-result">' +
                            '<div class="row">' +
                                '<div class="col s4">' +
                                    '<img class="responsive-img" :src="auction.mainImageSrcPath">' +
                                '</div>' +
                                '<div class="col s8">' +
                                    '<div class="row">' +
                                        '<a :href="\'/auction/get/\' + auction.id"><h5>{{ auction.title }}</h5></a>' +
                                    '</div>' +
                                    '<div class="row"></div>' +
                                        '<div class="row">' +
                                            '<div v-if="auction.winningBid != null && auction.isBid">' +
                                                '<h5 class="search-result-price">{{ auction.winningBid.price }} PLN</h5>' +
                                            '</div>' +
                                            '<div v-if="auction.winningBid == null && auction.isBid">' +
                                                '<h5 class="search-result-price">{{ auction.bidStartingPrice }} PLN</h5>' +
                                            '</div>' +
                                        '</div>' +
                                        '<div class="row">' +
                                            '<div v-if="auction.isBuyout && auction.isBid">' +
                                                '<h6 class="search-result-price">{{ auction.price }} PLN</h6>' +
                                                '<div class="buyout-label-with-bid">Kup teraz</div>' +
                                            '</div>' +
                                            '<div v-if="auction.isBuyout && !auction.isBid">' +
                                                '<h5 class="search-result-price">{{ auction.price }} PLN</h5>' +
                                                '<div class="buyout-label">Kup teraz</div>' +
                                                '</div>' +
                                            '</div>' +
                                        '<div class="row"></div>' +
                                        '<div class="row"></div>' +
                                        '<div class="row">' +
                                            '<div class="right" v-if="auction.isBid && auction.biddingUsersAmount > 0">{{ auction.biddingUsersAmount }} osoby licytują</div>' +
                                            '<div class="right" v-if="auction.isBid && auction.biddingUsersAmount == 0">nikt nie licytuje, bądź pierwszy</div>' +
                                        '</div>' +
                                    '<div class="row"></div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="divider"></div>' +
                        '</div>' +
                    '</div>' +
                    '<div v-else-if="auctions.length == 0" class="col s10">' +
                        '<h2>Wyniki wyszukiwania</h2>' +
                        '<h5>Brak wyników wyszukiwania</h5>' +
                    '</div>' +
                '</div>' +
            '</div>',

        methods: {
            previous: function() {
                var vm = this;
                if (vm.previousCategoryId === 0) {
                    vm.getCategories();
                    vm.showReturn = false;
                    vm.getSearchResults(vm.searchString, undefined);
                } else {
                    vm.getChildren(vm.previousCategoryId);
                    vm.getSearchResults(vm.searchString, vm.previousCategoryId);
                }
            },
            getSearchResults: function(searchString, categoryId) {
                var vm = this;
                var restUrl = "/search-rest?searchString=" + searchString + "&categoryId=" + categoryId;

                axios.get(restUrl)
                    .then(function (response) {
                        vm.auctions = response.data;
                        vm.prepareSrcAttributes();
                    })
                    .catch(function () {
                        vm.auctions = [];
                        this.errors.push(e)
                    })
            },
            getCategories: function() {
                var vm = this;
                if(vm.categoryId == undefined || vm.categoryId == "") {
                    vm.sendGetTopCategoriesRequest();
                } else {
                    vm.sendGetChildrenRequest(vm.categoryId);
                }
            },
            getChildren: function(id) {
                var vm = this;
                vm.sendGetChildrenRequest(id);
                vm.getSearchResults(vm.searchString, id)
            },
            sendGetChildrenRequest: function(id) {
                var vm = this;
                axios.get('/category-rest/get-children/' + id)
                    .then(function (response) {
                        vm.categories = response.data;
                        vm.previousCategoryId = vm.categories[0].parentOfParentId !== null ? vm.categories[0].parentOfParentId : 0;
                        vm.isLoading = false;
                        vm.showReturn = true;
                    })
                    .catch(function () {
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            sendGetTopCategoriesRequest: function () {
                var vm = this;
                axios.get('/category-rest/get-all-top')
                    .then(function (response) {
                        vm.categories = response.data;
                        vm.isLoading = false;
                        vm.showReturn = false;
                    })
                    .catch(function () {
                        this.errors.push(e);
                        console.log(e);
                    })
            },
            prepareSrcAttributes : function() {
                var vm = this;

                for(var i = 0; i < vm.auctions.length; i++) {
                    var auction = vm.auctions[i];
                    auction.mainImageSrcPath = "data:image/png;base64," + auction.mainImage;
                }
            }
        },

        mounted() {
            var vm = this;
            vm.searchString = $("#searchString").val();
            vm.categoryId = $("#searchCategory").val();
            vm.getSearchResults(vm.searchString, vm.categoryId);
            vm.getCategories();
        }
    });

    var app = new Vue({
        el: '#app',
    });
};