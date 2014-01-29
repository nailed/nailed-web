angular.module("nailed", [
    "nailed.directives",
    "nailed.controllers",
    "nailed.services"
])
//, controller: "PlayoutController"
.config(["$routeProvider", function(router){
    router.when("/", {templateUrl: "/pages/home.html"});
    router.when("/mappacks", {templateUrl: "/pages/mappacks.html"});
    router.when("/mappacks/:mappackName", {templateUrl: "/pages/mappackdetail.html", controller: "MappackDetailController"});
    router.otherwise({redirectTo: "/"})
}])
