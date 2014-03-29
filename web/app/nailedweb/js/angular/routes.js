angular.module("nailed", [
    "nailed.directives",
    "nailed.controllers",
    "nailed.services"
])
.config(["$routeProvider", function(router){
    router.when("/", {templateUrl: "/pages/home.html"});
    router.when("/mappacks", {templateUrl: "/pages/mappacks.html"});
    router.when("/mappacks/:mappackName", {templateUrl: "/pages/mappackdetail.html", controller: "MappackDetailController"});
    router.when("/register", {templateUrl: "/pages/register.html", controller: "RegisterController"});
    router.when("/linkMojang", {templateUrl: "/pages/mojangaccount.html", controller: "LinkMojangController", access:{isFree: false}});
    router.when("/login", {templateUrl: "/pages/login.html", controller: "LoginController"});
    router.otherwise({redirectTo: "/"})
}])
