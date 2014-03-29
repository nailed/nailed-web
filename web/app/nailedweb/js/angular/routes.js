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
    router.otherwise({redirectTo: "/"})
}])