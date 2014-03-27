var services = angular.module("nailed.services", []);

var UserService = services.factory('UserService', [
    "$http", function($http) {
        var ret = {
            loggedIn: false,
            email: '',
            session: '',
            fullname: '',
            login: function(email, password, callback) {
                return $http
                .post('/api/login/', "email=" + encodeURIComponent(email) + "&password=" + encodeURIComponent(password))
                .success(function(data, status, headers, config) {
                    if(data.status == "ok"){
                        ret.loggedIn = true;
                        ret.session = data.session.id;
                        ret.email = data.user.email;
                        ret.fullname = data.user.fullName;
                        callback(true, 'OK');
                        return;
                    }
                    return callback(false, data.error);
                }).error(function(data, status, headers, config) {
                    return callback(false, data.error);
                });
            },
            logout: function() {
                return $http.delete('/api/login/', {params: {session: ret.session}})
                .success(function(data, status, headers, config){
                    if(data.status == "ok"){
                        ret.loggedIn = false;
                        ret.session = "";
                        ret.email = "";
                        ret.fullname = "";
                    }
                });
            },
            onRegistered: function(data){
                this.loggedIn = true;
                this.email = data.user.email;
                this.fullname = data.user.fullName;
                this.session = data.session.id;
            }
        };
        return ret;
    }
]);
