var services = angular.module("nailed.services", []);

var UserService = services.factory('UserService', [
    "$http", function($http) {
        var ret = {
            loggedIn: false,
            username: '',
            session: '',
            fullname: '',
            login: function(username, password, callback) {
                return $http
                .post('/api/login/', "username=" + encodeURIComponent(username) + "&password=" + encodeURIComponent(password))
                .success(function(data, status, headers, config) {
                    if(data.status == "ok"){
                        ret.loggedIn = true;
                        ret.session = data.session.id;
                        ret.username = data.user.username;
                        ret.fullname = data.user.fullName;
                        callback(true, 'OK');
                        return;
                    }
                    return callback(false, data.error);
                }).error(function(data, status, headers, config) {
                    return callback(false, data.error);
                });
            },
            logout: function(callback) {
                return $http.delete('/api/login/').success(function(data, status, headers, config){
                    if(data.status == "ok"){
                        ret.loggedIn = false;
                        ret.session = "";
                        ret.username = "";
                        ret.fullname = "";
                        callback();
                    }
                });
            },
            register: function(data, callback) {
                return $http
                .post('/api/register/', "email=" + encodeURIComponent(data.email) + "&username=" + encodeURIComponent(data.username) + "&password=" + encodeURIComponent(data.password) + "&name=" + encodeURIComponent(data.name))
                .success(function(data, status, headers, config) {
                    if(data.status == "ok"){
                        ret.loggedIn = true;
                        ret.session = data.session.id;
                        ret.username = data.user.username;
                        ret.fullname = data.user.fullName;
                        callback(true, 'OK');
                        return;
                    }
                    return callback(false, data.error);
                }).error(function(data, status, headers, config) {
                    return callback(false, data.error);
                });
            }
        };
        return ret;
    }
]);
