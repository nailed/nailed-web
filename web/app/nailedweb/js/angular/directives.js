angular.module("nailed.directives", [])
    .directive("clickPage", function($location){
        return function(scope, element, attrs){
            var path;
            attrs.$observe("clickPage", function(val){
                path = val;
            });
            element.bind("click", function(){
                scope.$apply(function(){
                    $location.path(path);
                });
            });
        }
    })
    .directive("fileModel", function(){
        return function(scope, element, attrs){
            element.bind("change", function(changeEvent){
                scope.$apply(function(){
                    scope.files = changeEvent.target.files;
                });
            });
        }
    });
