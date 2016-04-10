var homeController = angular.module('homeController', []);

homeController.controller('homeCtrl', ['$http', '$log', '$scope', '$sessionStorage', '$location', 'webSocketFactory', 'homeFactory',
    function ($http, $log, $scope, $sessionStorage, $location, webSocketFactory, homeFactory) {

        function init() {
            if (!$sessionStorage.username || !$sessionStorage.sessionId) {
                $location.path('/login');
            }
            $scope.cards = cards.time;

            $scope.username = $sessionStorage.username;
            $scope.sessionId = $sessionStorage.sessionId;
            $scope.users = [
                {name: 'hazem'}, {name: 'khaireddine'}, {name: 'nico'}, {name: 'Seif'},
                {name: 'Raed'}, {name: 'aymen'}, {name: 'ahmed'}
            ];

            homeFactory.get($sessionStorage.sessionId, function (data) {
                    $log.info(data);
                    $scope.tickets = data.tickets;
                }
            );
        }

        $scope.logout = function () {
            $sessionStorage.$reset();
            webSocketFactory.disconnect();
            $location.path('/login');
        };

        init();
    }]);

homeController.resolve = {
    ws: ['webSocketFactory', '$q', function (webSocketFactory, $q) {
        var delay = $q.defer();
        webSocketFactory.connect();
        delay.resolve();
        return delay.promise;
    }]
};