"use strict";
/*@ngInject*/
angular.module('sessionFactory', [])
    .factory('sessionFactory', function ($http, $httpWrapper, $localStorage) {
        return {
            create: function (data, onSuccess, onError) {
                onSuccess = onSuccess || angular.noop;
                onError = onError || angular.noop;
                $httpWrapper.post('/sessions', data, function (body, status, headers) {
                    console.log('headers: ', headers);
                    var token = headers("jwt-token");
                    console.log('token: ', token);
                    if (token) {
                        // store username and token in local storage to keep user logged in between page refreshes
                        $localStorage.currentUser = {
                            username: body.username,
                            sessionId: body.sessionId,
                            admin: true,
                            token: token
                        };
                    }
                    onSuccess(body);
                }, onError);
            },
            get: function (onSuccess, onError) {
                onSuccess = onSuccess || angular.noop;
                onError = onError || angular.noop;
                var url = '/sessions';
                $httpWrapper.get(url, onSuccess, onError);
            }
        };
    });