$(function() {

    var players = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: 'players?query=%QUERY',
            wildcard: '%QUERY'
        }
    });

    $('.typeahead').typeahead(null, {
        name: 'players',
        display: 'display_name',
        source: players,
        highlight: true,
        hint: false
    });

    // init Isotope
    var $grid = $('.grid').isotope({
        itemSelector: '.grid-item',
        layoutMode: 'horiz'
    });

    // layout Isotope after each image loads
    $grid.imagesLoaded().progress( function() {
        $grid.isotope('layout');
    });

    function statOrAggregationChanged() {
        chart.update({
            title: {
                text: $('#' + selectedStat + "-item").text() + ' over time'
            },
            yAxis: {
                title: {
                    text: $('#' + selectedStat + "-item").text()
                }
            }
        });
        selected.forEach(function(player) {
            fetchData(player.id, function(data) {
                chart.series.forEach(function(series) {
                    if (series.name === player.display_name) {
                        series.setData(data)
                    }
                });
            });
        });
    }

    function fetchData(playerId, callback) {
        $.getJSON('data?playerId=' + playerId + '&stat=' + selectedStat + '&aggregation=' + selectedAggregation, function (data) {
            callback(data)
        });
    }

    function addPlayer(player) {
        // Add player picture to selected area
        var $item = $('<div class="grid-item" player_id="' + player.id + '"><img src="http://stats.nba.com/media/players/230x185/' + player.id + '.png"/><p class="name">' + player.display_name + '</div>');
        $grid.append($item)
        .isotope('appended', $item)
        .isotope();

        // Fetch data and create series on chart
        fetchData(player.id, function(data) {
            chart.addSeries({
                name: player.display_name,
                data: data
            });
            selected.push(player);
            Cookies.set('players', JSON.stringify(selected));
        });

        // Setup click listener to remove player
        $item.click(function() {
            id = $item.attr("player_id");
            var idx = -1;
            for(var i = 0; i < selected.length; i++) {
                if (selected[i].id == id) {
                    idx = i;
                    break;
                }
            }
            selected.splice(idx, 1);
            chart.series[idx].remove();
            $item.remove();
            $grid.isotope();
            Cookies.set('players', JSON.stringify(selected));
        });
    }

    $('.typeahead').bind('typeahead:select', function(ev, player) {
        $('.typeahead').typeahead('val', "");
        addPlayer(player);
    });



    $('#stat-search-input').on('change keyup paste', function() {
        $statgrid.isotope();
    });

    $('.filter-pill').click(function() {
        $(this).addClass('active');
        $(this).siblings().removeClass('active');
        $statgrid.isotope();
    });
    var selectedFilter = 'All'
        var $statgrid = $('#stat-grid').isotope({
            itemSelector: '.stat-grid-item',
            filter: function() {
                // Check search
                var searchTerm = $('#stat-search-input').val();
                var searchMatch = searchTerm == '' ||  $(this).text().indexOf(searchTerm) >= 0;

                // Check filter
                var activeFilter = $('#all-pill').hasClass('active') ? null : $('#traditional-pill').hasClass('active') ? 'traditional' : 'advanced';
                var filterMatch = activeFilter == null || $(this).hasClass(activeFilter);

                return searchMatch && filterMatch;
            }
        });

    $('.stat-grid-item').click(function() {
        $(this).addClass('selected');
        $(this).siblings().removeClass('selected');
        $('#stat-modal').modal('hide');
        selectedStat = $(this).attr('id').slice(0, -5);
        Cookies.set('stat', selectedStat)
        statOrAggregationChanged();
    });

    $('#aggregate-button').on('inserted.bs.popover', function () {
        $('#' + selectedAggregation + '-aggregation-radio').attr('checked', 'true');
        $('.popover input').click(function() {
            selectedAggregation = $(this).val();
            Cookies.set('aggregation', selectedAggregation);
            statOrAggregationChanged();
        });
    });


    $('#aggregate-button').popover({
        html: true,
        content: '<label class="radio-inline"> <input type="radio" name="aggregation-options" id="season-aggregation-radio" value="season" /> Season </label> <label class="radio-inline"> <input type="radio" name="aggregation-options" id="month-aggregation-radio" value="month" /> Month </label> <label class="radio-inline"> <input type="radio" name="aggregation-options" id="none-aggregation-radio" value="none" /> None </label>'
    });

    $('#stat-modal').on('shown.bs.modal', function (e) {
        $statgrid.isotope()
    })

    $('#stat-modal').on('hidden.bs.modal', function (e) {
        $('#stat-search-input').val('');
    })

    var selectedAggregation = Cookies.get("aggregation");
    if (selectedAggregation === undefined) {
        selectedAggregation = 'season';
        Cookies.set('aggregation', selectedAggregation);
    }

    var selectedStat = Cookies.get("stat");
    if (selectedStat === undefined) {
        selectedStat = 'points';
        Cookies.set("stat", selectedStat);
    }
    $('#' + selectedStat + "-item").addClass("selected");


    var selected = [];
    var playerJson = Cookies.get('players');
    if (playerJson != undefined) {
        JSON.parse(playerJson).forEach(function(player) {
            addPlayer(player);
        });
    }



    chart = Highcharts.chart('chart', {
        chart: {
            zoomType: 'x'
        },
        title: {
            text: $('#' + selectedStat + "-item").text() + ' over time'
        },
        subtitle: {
            text: document.ontouchstart === undefined ?
                    'Click and drag to zoom' : 'Pinch the chart to zoom'
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
            title: {
                text: $('#' + selectedStat + "-item").text()
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            series: {
                connectNulls: true
            }
        },
        tooltip: {
            formatter: function () {
                var d = new Date(this.x);
                var numDecimals = this.y > 1 ? 1 : 2;
                return this.series.name + '<br/>' + d.toLocaleString("en-us", { month: "short" }) + " '" + d.getFullYear().toString().substr(-2) + ': <b>' + this.y.toFixed(numDecimals) + '</b>';
            }
        },
    });
});