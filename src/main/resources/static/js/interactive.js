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
        display: 'name',
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
                text: $('#' + stat + "-item").text() + ' over time'
            },
            yAxis: {
                title: {
                    text: $('#' + stat + "-item").text()
                }
            }
        });
        selected.forEach(function(player) {
            fetchData(player.playerId, function(data) {
                chart.series.forEach(function(series) {
                    if (series.name === player.name) {
                        series.setData(data)
                    }
                });
            });
        });
    }

    function fetchData(playerId, callback) {
        $.getJSON('data?playerId=' + playerId + '&stat=' + stat + '&aggregation=' + aggregation, function (data) {
            callback(data)
        });
    }

    function addPlayer(player) {
        // Add player picture to selected area
        var $item = $('<div class="grid-item" player_id="' + player.playerId + '"><img src="/players/' + player.playerId + '/image"/><p class="name">' + player.name + '</div>');
        $grid.append($item)
        .isotope('appended', $item)
        .isotope();

        $name = $item.children('p.name').first();
//      $name.offset($item.height() - $name.height(), 0);
//      console.log($name.css('height'));

        // Fetch data and create series on chart
        fetchData(player.playerId, function(data) {
            chart.addSeries({
                name: player.name,
                data: data
            });
            selected.push(player);
            Cookies.set('players', selected.map(function(p) { return p.playerId}).join(','));
        });

        // Setup click listener to remove player
        $item.click(function() {
            id = $item.attr("player_id");
            var idx = -1;
            for(var i = 0; i < selected.length; i++) {
                if (selected[i].playerId == id) {
                    idx = i;
                    break;
                }
            }
            selected.splice(idx, 1);
            chart.series[idx].remove();
            $item.remove();
            $grid.isotope();
            Cookies.set('players', selected.map(function(p) {return p.playerId}).join(','));
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
        stat = $(this).attr('id').slice(0, -5);
        Cookies.set('stat', stat)
        statOrAggregationChanged();
    });

    $('#aggregate-button').on('inserted.bs.popover', function () {
        $('#' + aggregation + '-aggregation-radio').attr('checked', 'true');
        $('.popover input').click(function() {
            aggregation = $(this).val();
            Cookies.set('aggregation', aggregation);
            $('#aggregate-button').popover('hide');
            statOrAggregationChanged();
        });
    });

    $('#aggregate-button').click(function() {
        $(this).popover('toggle')
    }).popover({
        html: true,
        trigger: 'manual',
        content: '<label class="radio-inline"> <input type="radio" name="aggregation-options" id="season-aggregation-radio" value="season" /> Season </label> <label class="radio-inline"> <input type="radio" name="aggregation-options" id="month-aggregation-radio" value="month" /> Month </label> <label class="radio-inline"> <input type="radio" name="aggregation-options" id="none-aggregation-radio" value="none" /> None </label>'
    }).on('hidden.bs.popover', function() {
        setTimeout(function() {
            $('#aggregate-button').blur();
        }, 1);
    });

    $('#stat-modal').on('show.bs.modal', function (e) {
        $('#aggregate-button').popover('hide');
    });

    $('#stat-modal').on('shown.bs.modal', function (e) {
        $statgrid.isotope()
    });

    $('#stat-modal').on('hidden.bs.modal', function (e) {
        $('#stat-search-input').val('');
        setTimeout(function() {
            $('#stat-select-button').blur();
        }, 1);
    })

    $('#share-icon').click(function() {
        $.getJSON('share', function (data) {
            console.log('created ' + data.chartId);
        });
        $('#share-modal').modal('show');
    });

    $('#' + stat + "-item").addClass("selected");

    var selected = []
    toSelect.forEach(function(player) {
        addPlayer(player);
    });

    $('#share-icon').click(function() {
        console.log('Shared');
    });

    chart = Highcharts.chart('chart', {
        chart: {
            zoomType: 'x'
        },
        title: {
            text: $('#' + stat + "-item").text() + ' over time'
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
                text: $('#' + stat + "-item").text()
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            line: {
                marker: {
                    enabled: false,
                    symbol: 'circle'
                }
            },
            series: {
                connectNulls: false
            }
        },
        tooltip: {
            formatter: function () {
                var m = moment(this.x).add(23, 'hours');
                var numDecimals = this.y > 1 ? 1 : 2;
                var dateStr = '';
                if (aggregation == 'season') {
                    dateStr = m.format('YYYY');
                } else if (aggregation == 'month') {
                    dateStr = m.format("MMM 'YY");
                } else {
                    dateStr = m.format("MMM DD 'YY");
                }
                return this.series.name + '<br/>' + dateStr + ': <b>' + this.y.toFixed(numDecimals) + '</b>';
            }
        },
    });
});