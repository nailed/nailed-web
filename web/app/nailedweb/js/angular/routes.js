angular.module("nailed", [
    "nailed.directives",
    "nailed.controllers",
    "nailed.services"
])
.config(["$routeProvider", function(router){
    router.when("/", {templateUrl: "/pages/home.html"});
    router.when("/mappacks", {templateUrl: "/pages/mappacks.html", controller: "MappackListController"});
    router.when("/mappacks/$createNew", {templateUrl: "/pages/mappackcreate.html", controller: "MappackCreateController"/*, access:{isFree: false, permission:"createMappack"}*/});
    router.when("/mappacks/:mappackName", {templateUrl: "/pages/mappackdetail.html", controller: "MappackDetailController"});
    router.when("/register", {templateUrl: "/pages/register.html", controller: "RegisterController"});
    router.when("/linkMojang", {templateUrl: "/pages/mojangaccount.html", controller: "LinkMojangController", access:{isFree: false}});
    router.when("/login", {templateUrl: "/pages/login.html", controller: "LoginController"});
    router.otherwise({redirectTo: "/"})
}])
