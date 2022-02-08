(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof module === 'object' && module.exports) {
        module.exports = factory(require('jquery'));
    } else {
        factory(window.jQuery);
    }
}(function ($) {
    $.extend($.summernote.plugins, {
        'emoji': function (context) {
            var self = this;
            var ui = $.summernote.ui;
            //全部
			//var emojis = ['wink2', 'bowtie', 'smile', 'laughing', 'blush', 'smiley', 'relaxed', 'smirk', 'heart_eyes', 'kissing_heart', 'kissing_closed_eyes', 'flushed', 'relieved', 'satisfied', 'grin', 'wink', 'stuck_out_tongue_winking_eye', 'stuck_out_tongue_closed_eyes', 'grinning', 'kissing', 'kissing_smiling_eyes', 'stuck_out_tongue', 'sleeping', 'worried', 'frowning', 'anguished', 'open_mouth', 'grimacing', 'confused', 'hushed', 'expressionless', 'unamused', 'sweat_smile', 'sweat', 'disappointed_relieved', 'weary', 'pensive', 'disappointed', 'confounded', 'fearful', 'cold_sweat', 'persevere', 'cry', 'sob', 'joy', 'astonished', 'scream', 'neckbeard', 'tired_face', 'angry', 'rage', 'triumph', 'sleepy', 'yum', 'mask', 'sunglasses', 'dizzy_face', 'imp', 'smiling_imp', 'neutral_face', 'no_mouth', 'innocent', 'alien', 'yellow_heart', 'blue_heart', 'purple_heart', 'heart', 'green_heart', 'broken_heart', 'heartbeat', 'heartpulse', 'two_hearts', 'revolving_hearts', 'cupid', 'sparkling_heart', 'sparkles', 'star', 'star2', 'dizzy', 'boom', 'collision', 'anger', 'exclamation', 'question', 'grey_exclamation', 'grey_question', 'zzz', 'dash', 'sweat_drops', 'notes', 'musical_note', 'fire', 'hankey', 'poop', 'shit', '+1', 'thumbsup', '-1', 'thumbsdown', 'ok_hand', 'punch', 'facepunch', 'fist', 'v', 'wave', 'hand', 'raised_hand', 'open_hands', 'point_up', 'point_down', 'point_left', 'point_right', 'raised_hands', 'pray', 'point_up_2', 'clap', 'muscle', 'metal', 'walking', 'runner', 'running', 'couple', 'family', 'two_men_holding_hands', 'two_women_holding_hands', 'dancer', 'dancers', 'ok_woman', 'no_good', 'information_desk_person', 'raising_hand', 'bride_with_veil', 'person_with_pouting_face', 'person_frowning', 'bow', 'couplekiss', 'couple_with_heart', 'massage', 'haircut', 'nail_care', 'boy', 'girl', 'woman', 'man', 'baby', 'older_woman', 'older_man', 'person_with_blond_hair', 'man_with_gua_pi_mao', 'man_with_turban', 'construction_worker', 'cop', 'angel', 'princess', 'smiley_cat', 'smile_cat', 'heart_eyes_cat', 'kissing_cat', 'smirk_cat', 'scream_cat', 'crying_cat_face', 'joy_cat', 'pouting_cat', 'japanese_ogre', 'japanese_goblin', 'see_no_evil', 'hear_no_evil', 'speak_no_evil', 'guardsman', 'skull', 'feet', 'lips', 'kiss', 'droplet', 'ear', 'eyes', 'nose', 'tongue', 'love_letter', 'bust_in_silhouette', 'busts_in_silhouette', 'speech_balloon', 'thought_balloon', 'feelsgood', 'finnadie', 'goberserk', 'godmode', 'hurtrealbad', 'rage1', 'rage2', 'rage3', 'rage4', 'suspect', 'trollface', 'sunny', 'umbrella', 'cloud', 'snowflake', 'snowman', 'zap', 'cyclone', 'foggy', 'ocean', 'cat', 'dog', 'mouse', 'hamster', 'rabbit', 'wolf', 'frog', 'tiger', 'koala', 'bear', 'pig', 'pig_nose', 'cow', 'boar', 'monkey_face', 'monkey', 'horse', 'racehorse', 'camel', 'sheep', 'elephant', 'panda_face', 'snake', 'bird', 'baby_chick', 'hatched_chick', 'hatching_chick', 'chicken', 'penguin', 'turtle', 'bug', 'honeybee', 'ant', 'beetle', 'snail', 'octopus', 'tropical_fish', 'fish', 'whale', 'whale2', 'dolphin', 'cow2', 'ram', 'rat', 'water_buffalo', 'tiger2', 'rabbit2', 'dragon', 'goat', 'rooster', 'dog2', 'pig2', 'mouse2', 'ox', 'dragon_face', 'blowfish', 'crocodile', 'dromedary_camel', 'leopard', 'cat2', 'poodle', 'paw_prints', 'bouquet', 'cherry_blossom', 'tulip', 'four_leaf_clover', 'rose', 'sunflower', 'hibiscus', 'maple_leaf', 'leaves', 'fallen_leaf', 'herb', 'mushroom', 'cactus', 'palm_tree', 'evergreen_tree', 'deciduous_tree', 'chestnut', 'seedling', 'blossom', 'ear_of_rice', 'shell', 'globe_with_meridians', 'sun_with_face', 'full_moon_with_face', 'new_moon_with_face', 'new_moon', 'waxing_crescent_moon', 'first_quarter_moon', 'waxing_gibbous_moon', 'full_moon', 'waning_gibbous_moon', 'last_quarter_moon', 'waning_crescent_moon', 'last_quarter_moon_with_face', 'first_quarter_moon_with_face', 'moon', 'earth_africa', 'earth_americas', 'earth_asia', 'volcano', 'milky_way', 'partly_sunny', 'octocat', 'squirrel', 'bamboo', 'gift_heart', 'dolls', 'school_satchel', 'mortar_board', 'flags', 'fireworks', 'sparkler', 'wind_chime', 'rice_scene', 'jack_o_lantern', 'ghost', 'santa', 'christmas_tree', 'gift', 'bell', 'no_bell', 'tanabata_tree', 'tada', 'confetti_ball', 'balloon', 'crystal_ball', 'cd', 'dvd', 'floppy_disk', 'camera', 'video_camera', 'movie_camera', 'computer', 'tv', 'iphone', 'phone', 'telephone', 'telephone_receiver', 'pager', 'fax', 'minidisc', 'vhs', 'sound', 'speaker', 'mute', 'loudspeaker', 'mega', 'hourglass', 'hourglass_flowing_sand', 'alarm_clock', 'watch', 'radio', 'satellite', 'loop', 'mag', 'mag_right', 'unlock', 'lock', 'lock_with_ink_pen', 'closed_lock_with_key', 'key', 'bulb', 'flashlight', 'high_brightness', 'low_brightness', 'electric_plug', 'battery', 'calling', 'email', 'mailbox', 'postbox', 'bath', 'bathtub', 'shower', 'toilet', 'wrench', 'nut_and_bolt', 'hammer', 'seat', 'moneybag', 'yen', 'dollar', 'pound', 'euro', 'credit_card', 'money_with_wings', 'e-mail', 'inbox_tray', 'outbox_tray', 'envelope', 'incoming_envelope', 'postal_horn', 'mailbox_closed', 'mailbox_with_mail', 'mailbox_with_no_mail', 'door', 'smoking', 'bomb', 'gun', 'hocho', 'pill', 'syringe', 'page_facing_up', 'page_with_curl', 'bookmark_tabs', 'bar_chart', 'chart_with_upwards_trend', 'chart_with_downwards_trend', 'scroll', 'clipboard', 'calendar', 'date', 'card_index', 'file_folder', 'open_file_folder', 'scissors', 'pushpin', 'paperclip', 'black_nib', 'pencil2', 'straight_ruler', 'triangular_ruler', 'closed_book', 'green_book', 'blue_book', 'orange_book', 'notebook', 'notebook_with_decorative_cover', 'ledger', 'books', 'bookmark', 'name_badge', 'microscope', 'telescope', 'newspaper', 'football', 'basketball', 'soccer', 'baseball', 'tennis', '8ball', 'rugby_football', 'bowling', 'golf', 'mountain_bicyclist', 'bicyclist', 'horse_racing', 'snowboarder', 'swimmer', 'surfer', 'ski', 'spades', 'hearts', 'clubs', 'diamonds', 'gem', 'ring', 'trophy', 'musical_score', 'musical_keyboard', 'violin', 'space_invader', 'video_game', 'black_joker', 'flower_playing_cards', 'game_die', 'dart', 'mahjong', 'clapper', 'memo', 'pencil', 'book', 'art', 'microphone', 'headphones', 'trumpet', 'saxophone', 'guitar', 'shoe', 'sandal', 'high_heel', 'lipstick', 'boot', 'shirt', 'tshirt', 'necktie', 'womans_clothes', 'dress', 'running_shirt_with_sash', 'jeans', 'kimono', 'bikini', 'ribbon', 'tophat', 'crown', 'womans_hat', 'mans_shoe', 'closed_umbrella', 'briefcase', 'handbag', 'pouch', 'purse', 'eyeglasses', 'fishing_pole_and_fish', 'coffee', 'tea', 'sake', 'baby_bottle', 'beer', 'beers', 'cocktail', 'tropical_drink', 'wine_glass', 'fork_and_knife', 'pizza', 'hamburger', 'fries', 'poultry_leg', 'meat_on_bone', 'spaghetti', 'curry', 'fried_shrimp', 'bento', 'sushi', 'fish_cake', 'rice_ball', 'rice_cracker', 'rice', 'ramen', 'stew', 'oden', 'dango', 'egg', 'bread', 'doughnut', 'custard', 'icecream', 'ice_cream', 'shaved_ice', 'birthday', 'cake', 'cookie', 'chocolate_bar', 'candy', 'lollipop', 'honey_pot', 'apple', 'green_apple', 'tangerine', 'lemon', 'cherries', 'grapes', 'watermelon', 'strawberry', 'peach', 'melon', 'banana', 'pear', 'pineapple', 'sweet_potato', 'eggplant', 'tomato', 'corn', 'house', 'house_with_garden', 'school', 'office', 'post_office', 'hospital', 'bank', 'convenience_store', 'love_hotel', 'hotel', 'wedding', 'church', 'department_store', 'european_post_office', 'city_sunrise', 'city_sunset', 'japanese_castle', 'european_castle', 'tent', 'factory', 'tokyo_tower', 'japan', 'mount_fuji', 'sunrise_over_mountains', 'sunrise', 'stars', 'statue_of_liberty', 'bridge_at_night', 'carousel_horse', 'rainbow', 'ferris_wheel', 'fountain', 'roller_coaster', 'ship', 'speedboat', 'boat', 'sailboat', 'rowboat', 'anchor', 'rocket', 'airplane', 'helicopter', 'steam_locomotive', 'tram', 'mountain_railway', 'bike', 'aerial_tramway', 'suspension_railway', 'mountain_cableway', 'tractor', 'blue_car', 'oncoming_automobile', 'car', 'red_car', 'taxi', 'oncoming_taxi', 'articulated_lorry', 'bus', 'oncoming_bus', 'rotating_light', 'police_car', 'oncoming_police_car', 'fire_engine', 'ambulance', 'minibus', 'truck', 'train', 'station', 'train2', 'bullettrain_front', 'bullettrain_side', 'light_rail', 'monorail', 'railway_car', 'trolleybus', 'ticket', 'fuelpump', 'vertical_traffic_light', 'traffic_light', 'warning', 'construction', 'beginner', 'atm', 'slot_machine', 'busstop', 'barber', 'hotsprings', 'checkered_flag', 'crossed_flags', 'izakaya_lantern', 'moyai', 'circus_tent', 'performing_arts', 'round_pushpin', 'triangular_flag_on_post', '100', '109', '1234', 'abc', 'abcd', 'accept', 'aquarius', 'aries', 'arrows_clockwise', 'arrows_counterclockwise', 'arrow_backward', 'arrow_double_down', 'arrow_double_up', 'arrow_down', 'arrow_down_small', 'arrow_forward', 'arrow_heading_down', 'arrow_heading_up', 'arrow_left', 'arrow_lower_left', 'arrow_lower_right', 'arrow_right', 'arrow_right_hook', 'arrow_up', 'arrow_upper_left', 'arrow_upper_right', 'arrow_up_down', 'arrow_up_small', 'baby_symbol', 'baggage_claim', 'ballot_box_with_check', 'bangbang', 'black_circle', 'black_square', 'black_square_button', 'cancer', 'capital_abcd', 'capricorn', 'children_crossing', 'cinema', 'clock1', 'clock10', 'clock1030', 'clock11', 'clock1130', 'clock12', 'clock1230', 'clock130', 'clock2', 'clock230', 'clock3', 'clock330', 'clock4', 'clock430', 'clock5', 'clock530', 'clock6', 'clock630', 'clock7', 'clock730', 'clock8', 'clock830', 'clock9', 'clock930', 'cn', 'congratulations', 'cool', 'copyright', 'curly_loop', 'currency_exchange', 'customs', 'diamond_shape_with_a_dot_inside', 'do_not_litter', 'eight', 'eightball', 'eight_pointed_black_star', 'eight_spoked_asterisk', 'fast_forward', 'five', 'free', 'gemini', 'hash', 'heart_decoration', 'heavy_check_mark', 'heavy_division_sign', 'heavy_dollar_sign', 'heavy_exclamation_mark', 'heavy_minus_sign', 'heavy_multiplication_x', 'heavy_plus_sign', 'ideograph_advantage', 'information_source', 'interrobang', 'jp', 'keycap_ten', 'koko', 'kr', 'large_blue_circle', 'large_blue_diamond', 'large_orange_diamond', 'leftwards_arrow_with_hook', 'left_luggage', 'left_right_arrow', 'libra', 'link', 'mens', 'metro', 'mobile_phone_off', 'negative_squared_cross_mark', 'nine', 'non-potable_water', 'no_bicycles', 'no_entry', 'no_entry_sign', 'no_mobile_phones', 'no_pedestrians', 'no_smoking', 'o2', 'onehundred', 'onetwothreefour', 'onezeronine', 'ophiuchus', 'parking', 'part_alternation_mark', 'passport_control', 'pisces', 'potable_water', 'put_litter_in_its_place', 'radio_button', 'recycle', 'red_circle', 'registered', 'repeat', 'repeat_one', 'restroom', 'rewind', 'sagittarius', 'scorpius', 'secret', 'seven', 'shipit', 'signal_strength', 'six', 'six_pointed_star', 'small_blue_diamond', 'small_orange_diamond', 'small_red_triangle', 'small_red_triangle_down', 'soon', 'sos', 'symbols', 'taurus', 'three', 'trident', 'twisted_rightwards_arrows', 'u5272', 'u5408', 'u55b6', 'u6307', 'u6708', 'u6709', 'u6e80', 'u7121', 'u7533', 'u7981', 'u7a7a', 'uk', 'underage', 'vibration_mode', 'virgo', 'vs', 'wavy_dash', 'wc', 'wheelchair', 'white_check_mark', 'white_circle', 'white_flower', 'white_square', 'white_square_button', 'womens', 'zero'];
            //部分
            var emojis = ['wink2', 'bowtie', 'smile', 'laughing', 'blush', 'smiley', 'relaxed', 'smirk', 'heart_eyes', 'kissing_heart', 'kissing_closed_eyes', 'flushed', 'relieved', 'satisfied', 'grin', 'wink', 'stuck_out_tongue_winking_eye', 'stuck_out_tongue_closed_eyes', 'grinning', 'kissing', 'kissing_smiling_eyes', 'stuck_out_tongue', 'sleeping', 'worried', 'frowning', 'anguished', 'open_mouth', 'grimacing', 'confused', 'hushed', 'expressionless', 'unamused', 'sweat_smile', 'sweat', 'disappointed_relieved', 'weary', 'pensive', 'disappointed', 'confounded', 'fearful', 'cold_sweat', 'persevere', 'cry', 'sob', 'joy', 'astonished', 'scream', 'neckbeard', 'tired_face', 'angry', 'rage', 'triumph', 'sleepy', 'yum', 'mask', 'sunglasses', 'dizzy_face', 'imp', 'smiling_imp', 'neutral_face', 'no_mouth', 'innocent', 'alien', 'yellow_heart', 'blue_heart', 'purple_heart', 'heart', 'green_heart', 'broken_heart', 'heartbeat', 'heartpulse', 'two_hearts', 'revolving_hearts', 'cupid', 'sparkling_heart', 'sparkles', 'star', 'star2', 'dizzy', 'boom', 'collision', 'anger', 'exclamation', 'question', 'grey_exclamation', 'grey_question', 'zzz', 'dash', 'sweat_drops', 'notes', 'musical_note', 'fire', 'hankey', 'poop', 'shit', '+1', 'thumbsup', '-1', 'thumbsdown', 'ok_hand', 'punch', 'facepunch', 'fist', 'v', 'wave', 'hand', 'raised_hand', 'open_hands', 'point_up', 'point_down', 'point_left', 'point_right', 'raised_hands', 'pray', 'point_up_2', 'clap', 'muscle', 'metal', 'walking', 'runner', 'running', 'couple', 'family', 'two_men_holding_hands', 'two_women_holding_hands', 'dancer', 'dancers', 'ok_woman', 'no_good', 'information_desk_person', 'raising_hand', 'bride_with_veil', 'person_with_pouting_face', 'person_frowning', 'bow', 'couplekiss', 'couple_with_heart', 'massage', 'haircut', 'nail_care', 'boy', 'girl', 'woman', 'man', 'baby', 'older_woman', 'older_man', 'person_with_blond_hair', 'man_with_gua_pi_mao', 'man_with_turban', 'construction_worker', 'cop', 'angel', 'princess', 'smiley_cat', 'smile_cat', 'heart_eyes_cat', 'kissing_cat', 'smirk_cat', 'scream_cat', 'crying_cat_face', 'joy_cat', 'pouting_cat', 'japanese_ogre', 'japanese_goblin', 'see_no_evil', 'hear_no_evil', 'speak_no_evil', 'guardsman', 'skull', 'feet', 'lips', 'kiss', 'droplet', 'ear', 'eyes', 'nose', 'tongue', 'love_letter', 'bust_in_silhouette', 'busts_in_silhouette', 'speech_balloon', 'thought_balloon', 'feelsgood', 'finnadie', 'goberserk', 'godmode', 'hurtrealbad', 'rage1', 'rage2', 'rage3', 'rage4', 'suspect', 'trollface', 'sunny', 'umbrella', 'cloud', 'snowflake', 'snowman', 'zap', 'cyclone', 'foggy', 'ocean', 'cat', 'dog', 'mouse', 'hamster', 'rabbit', 'wolf', 'frog', 'tiger', 'koala', 'bear', 'pig', 'pig_nose', 'cow', 'boar', 'monkey_face', 'monkey', 'horse', 'racehorse', 'camel', 'sheep', 'elephant', 'panda_face', 'snake', 'bird', 'baby_chick', 'hatched_chick', 'hatching_chick', 'chicken', 'penguin', 'turtle', 'bug', 'honeybee', 'ant', 'beetle', 'snail', 'octopus', 'tropical_fish', 'fish', 'whale', 'whale2', 'dolphin', 'cow2', 'ram', 'rat', 'water_buffalo', 'tiger2', 'rabbit2', 'dragon', 'goat', 'rooster', 'dog2', 'pig2', 'mouse2', 'ox', 'dragon_face', 'blowfish', 'crocodile', 'dromedary_camel', 'leopard', 'cat2', 'poodle', 'paw_prints', 'bouquet', 'cherry_blossom', 'tulip', 'four_leaf_clover', 'rose', 'sunflower', 'hibiscus', 'maple_leaf', 'leaves', 'fallen_leaf', 'herb', 'mushroom', 'cactus', 'palm_tree', 'evergreen_tree', 'deciduous_tree', 'chestnut', 'seedling', 'blossom', 'ear_of_rice', 'shell', 'globe_with_meridians', 'sun_with_face', 'full_moon_with_face', 'new_moon_with_face', 'new_moon', 'waxing_crescent_moon', 'first_quarter_moon', 'waxing_gibbous_moon', 'full_moon', 'waning_gibbous_moon', 'last_quarter_moon', 'waning_crescent_moon', 'last_quarter_moon_with_face', 'first_quarter_moon_with_face', 'moon', 'earth_africa', 'earth_americas', 'earth_asia', 'volcano', 'milky_way', 'partly_sunny', 'octocat', 'squirrel', 'bamboo', 'gift_heart', 'dolls', 'school_satchel', 'mortar_board', 'flags', 'fireworks', 'sparkler', 'wind_chime', 'rice_scene', 'jack_o_lantern', 'ghost', 'santa', 'christmas_tree', 'gift', 'bell', 'no_bell', 'tanabata_tree', 'tada', 'confetti_ball', 'balloon', 'crystal_ball', 'cd', 'dvd', 'floppy_disk', 'camera', 'video_camera', 'movie_camera', 'computer', 'tv', 'iphone', 'phone', 'telephone', 'telephone_receiver', 'pager', 'fax', 'minidisc', 'vhs', 'sound', 'speaker', 'mute', 'loudspeaker', 'mega', 'hourglass', 'hourglass_flowing_sand', 'alarm_clock', 'watch', 'radio', 'satellite', 'loop', 'mag', 'mag_right', 'unlock', 'lock', 'lock_with_ink_pen', 'closed_lock_with_key', 'key', 'bulb', 'flashlight', 'high_brightness', 'low_brightness', 'electric_plug', 'battery', 'calling', 'email', 'mailbox', 'postbox', 'bath', 'bathtub', 'shower', 'toilet', 'wrench', 'nut_and_bolt', 'hammer', 'seat', 'moneybag', 'yen', 'dollar', 'pound', 'euro', 'credit_card', 'money_with_wings', 'e-mail', 'inbox_tray', 'outbox_tray', 'envelope', 'incoming_envelope', 'postal_horn', 'mailbox_closed', 'mailbox_with_mail', 'mailbox_with_no_mail', 'door', 'smoking', 'bomb', 'gun', 'hocho', 'pill', 'syringe', 'page_facing_up', 'page_with_curl', 'bookmark_tabs', 'bar_chart', 'chart_with_upwards_trend', 'chart_with_downwards_trend', 'scroll', 'clipboard', 'calendar', 'date', 'card_index', 'file_folder', 'open_file_folder', 'scissors', 'pushpin', 'paperclip', 'black_nib', 'pencil2', 'straight_ruler', 'triangular_ruler', 'closed_book', 'green_book', 'blue_book', 'orange_book', 'notebook', 'notebook_with_decorative_cover', 'ledger', 'books', 'bookmark', 'name_badge', 'microscope', 'telescope', 'newspaper', 'football', 'basketball', 'soccer', 'baseball', 'tennis', '8ball', 'rugby_football', 'bowling', 'golf', 'mountain_bicyclist', 'bicyclist', 'horse_racing', 'snowboarder', 'swimmer', 'surfer', 'ski', 'spades', 'hearts', 'clubs', 'diamonds', 'gem', 'ring', 'trophy', 'musical_score', 'musical_keyboard', 'violin', 'space_invader', 'video_game', 'black_joker', 'flower_playing_cards', 'game_die', 'dart', 'mahjong', 'clapper', 'memo', 'pencil', 'book', 'art', 'microphone', 'headphones', 'trumpet', 'saxophone', 'guitar', 'shoe', 'sandal', 'high_heel', 'lipstick', 'boot', 'shirt', 'tshirt', 'necktie', 'womans_clothes', 'dress', 'running_shirt_with_sash', 'jeans', 'kimono', 'bikini', 'ribbon', 'tophat', 'crown', 'womans_hat', 'mans_shoe', 'closed_umbrella', 'briefcase', 'handbag', 'pouch', 'purse', 'eyeglasses', 'fishing_pole_and_fish', 'coffee', 'tea', 'sake', 'baby_bottle', 'beer', 'beers', 'cocktail', 'tropical_drink', 'wine_glass', 'fork_and_knife', 'pizza', 'hamburger', 'fries', 'poultry_leg', 'meat_on_bone', 'spaghetti', 'curry', 'fried_shrimp', 'bento', 'sushi', 'fish_cake', 'rice_ball', 'rice_cracker', 'rice', 'ramen', 'stew', 'oden', 'dango', 'egg', 'bread', 'doughnut', 'custard', 'icecream', 'ice_cream', 'shaved_ice', 'birthday', 'cake', 'cookie', 'chocolate_bar', 'candy', 'lollipop', 'honey_pot', 'apple', 'green_apple', 'tangerine', 'lemon', 'cherries', 'grapes', 'watermelon', 'strawberry', 'peach', 'melon', 'banana', 'pear', 'pineapple', 'sweet_potato', 'eggplant', 'tomato', 'corn', 'house', 'house_with_garden', 'school', 'office', 'post_office', 'hospital', 'bank', 'convenience_store', 'love_hotel', 'hotel', 'wedding', 'church', 'department_store', 'european_post_office', 'city_sunrise', 'city_sunset', 'japanese_castle', 'european_castle', 'tent', 'factory', 'tokyo_tower', 'japan', 'mount_fuji', 'sunrise_over_mountains', 'sunrise', 'stars', 'statue_of_liberty', 'bridge_at_night', 'carousel_horse', 'rainbow', 'ferris_wheel', 'fountain', 'roller_coaster', 'ship', 'speedboat', 'boat', 'sailboat', 'rowboat', 'anchor', 'rocket', 'airplane', 'helicopter', 'steam_locomotive', 'tram', 'mountain_railway', 'bike', 'aerial_tramway', 'suspension_railway', 'mountain_cableway', 'tractor', 'blue_car', 'oncoming_automobile', 'car', 'red_car', 'taxi', 'oncoming_taxi', 'articulated_lorry', 'bus', 'oncoming_bus', 'rotating_light', 'police_car', 'oncoming_police_car', 'fire_engine', 'ambulance', 'minibus', 'truck', 'train', 'station', 'train2', 'bullettrain_front', 'bullettrain_side', 'light_rail', 'monorail', 'railway_car', 'trolleybus', 'ticket', 'fuelpump', 'vertical_traffic_light', 'traffic_light', 'warning', 'construction', 'beginner', 'atm', 'slot_machine', 'busstop', 'barber', 'hotsprings', 'checkered_flag', 'crossed_flags', 'izakaya_lantern', 'moyai', 'circus_tent', 'performing_arts', 'round_pushpin', 'triangular_flag_on_post', '100', '109', '1234', 'abc', 'abcd', 'accept', 'aquarius', 'aries', 'arrows_clockwise', 'arrows_counterclockwise', 'arrow_backward', 'arrow_double_down', 'arrow_double_up', 'arrow_down', 'arrow_down_small', 'arrow_forward', 'arrow_heading_down', 'arrow_heading_up', 'arrow_left', 'arrow_lower_left', 'arrow_lower_right', 'arrow_right', 'arrow_right_hook', 'arrow_up', 'arrow_upper_left', 'arrow_upper_right', 'arrow_up_down', 'arrow_up_small', 'baby_symbol', 'baggage_claim', 'ballot_box_with_check', 'bangbang', 'black_circle', 'black_square', 'black_square_button', 'cancer', 'capital_abcd', 'capricorn', 'children_crossing', 'cinema', 'clock1', 'clock10', 'clock1030', 'clock11', 'clock1130', 'clock12', 'clock1230', 'clock130', 'clock2', 'clock230', 'clock3', 'clock330', 'clock4', 'clock430', 'clock5', 'clock530', 'clock6', 'clock630', 'clock7', 'clock730', 'clock8', 'clock830', 'clock9', 'clock930', 'cn', 'congratulations', 'cool', 'copyright', 'curly_loop', 'currency_exchange', 'customs', 'diamond_shape_with_a_dot_inside', 'do_not_litter', 'eight', 'eightball', 'eight_pointed_black_star', 'eight_spoked_asterisk', 'fast_forward', 'five', 'free', 'gemini', 'hash', 'heart_decoration', 'heavy_check_mark', 'heavy_division_sign', 'heavy_dollar_sign', 'heavy_exclamation_mark', 'heavy_minus_sign', 'heavy_multiplication_x', 'heavy_plus_sign', 'ideograph_advantage', 'information_source', 'interrobang', 'jp', 'keycap_ten', 'koko', 'kr', 'large_blue_circle', 'large_blue_diamond', 'large_orange_diamond', 'leftwards_arrow_with_hook', 'left_luggage', 'left_right_arrow', 'libra', 'link', 'mens', 'metro', 'mobile_phone_off', 'negative_squared_cross_mark', 'nine', 'non-potable_water', 'no_bicycles', 'no_entry', 'no_entry_sign', 'no_mobile_phones', 'no_pedestrians', 'no_smoking', 'o2', 'onehundred', 'onetwothreefour', 'onezeronine', 'ophiuchus', 'parking', 'part_alternation_mark', 'passport_control', 'pisces', 'potable_water', 'put_litter_in_its_place', 'radio_button', 'recycle', 'red_circle', 'registered', 'repeat', 'repeat_one', 'restroom', 'rewind', 'sagittarius', 'scorpius', 'secret', 'seven', 'shipit', 'signal_strength', 'six', 'six_pointed_star', 'small_blue_diamond', 'small_orange_diamond', 'small_red_triangle', 'small_red_triangle_down', 'soon', 'sos', 'symbols', 'taurus', 'three', 'trident', 'twisted_rightwards_arrows', 'u5272', 'u5408', 'u55b6', 'u6307', 'u6708', 'u6709', 'u6e80', 'u7121', 'u7533', 'u7981', 'u7a7a', 'uk', 'underage', 'wavy_dash', 'vibration_mode', 'virgo', 'vs', 'wc', 'wheelchair', 'white_check_mark', 'womens', 'zero'];

            var chunk = function (val, chunkSize) {
                var R = [];
                for (var i = 0; i < val.length; i += chunkSize)
                    R.push(val.slice(i, i + chunkSize));
                return R;
            };

            /*IE polyfill*/
            if (!Array.prototype.filter) {
                Array.prototype.filter = function (fun /*, thisp*/) {
                    var len = this.length >>> 0;
                    if (typeof fun != "function")
                        throw new TypeError();

                    var res = [];
                    var thisp = arguments[1];
                    for (var i = 0; i < len; i++) {
                        if (i in this) {
                            var val = this[i];
                            if (fun.call(thisp, val, i, this))
                                res.push(val);
                        }
                    }
                    return res;
                };
            }

            var addListener = function () {
                $('body').on('click', '#emoji-filter', function (e) {
                    e.stopPropagation();
                    $('#emoji-filter').focus();
                });
                $('body').on('keyup', '#emoji-filter', function (e) {
                    var filteredList = filterEmoji($('#emoji-filter').val());
                    $("#emoji-dropdown .emoji-list").html(filteredList);
                });
                $(document).on('click', '.closeEmoji', function(){
                    self.$panel.hide();
                });
                $(document).on('click', '.selectEmoji', function(){
                    var img = new Image();
                    //这里的路径需要修改
                    img.src = 'summernote/plugin/emoji/pngs/'+$(this).attr('data-value')+'.png';
                    // img.src = 'http://localhost/summernote/plugin/emoji/pngs/'+$(this).attr('data-value')+'.png';
                    img.alt = $(this).attr('data-value');
                    img.className = 'emoji-icon-inline';
                    context.invoke('editor.insertNode', img);

                });
            };

            var render = function (emojis) {
                var emoList = '';
                /*limit list to 24 images*/
                var emojis = emojis;
                var chunks = chunk(emojis, 6);
                for (j = 0; j < chunks.length; j++) {
                    emoList += '<div class="row m-0-l m-0-r">';
                    for (var i = 0; i < chunks[j].length; i++) {
                        var emo = chunks[j][i];
                        emoList += '<div class="col-xs-2">' +
                        '<a href="javascript:void(0)" class="selectEmoji closeEmoji" data-value="' + emo + '"><span class="emoji-icon" style="background-image: url(\'' + document.emojiSource + emo + '.png\');"></span></a>' +
                        '</div>';
                    }
                    emoList += '</div>';
                }

                return emoList;
            };

            var filterEmoji = function (value) {
                var filtered = emojis.filter(function (el) {
                    return el.indexOf(value) > -1;
                });
                return render(filtered);
            };

            // add emoji button
            context.memo('button.emoji', function () {
                // create button
                var button = ui.button({
                    contents: '<i class="fa fa-smile-o"/>',
                    tooltip: 'emoji',
                    click: function () {
                        if(document.emojiSource === undefined)
                            document.emojiSource = '';

                        self.$panel.show();
                    }
                });

                // create jQuery object from button instance.
                var $emoji = button.render();
                return $emoji;
            });
            

            // This events will be attached when editor is initialized.
            this.events = {
                // This will be called after modules are initialized.
                'summernote.init': function (we, e) {
                    addListener();
                },
                // This will be called when user releases a key on editable.
                'summernote.keyup': function (we, e) {
                }
            };

            // This method will be called when editor is initialized by $('..').summernote();
            // You can create elements for plugin
            this.initialize = function () {
                this.$panel = $('<div class="dropdown-menu dropdown-keep-open emoji-dialog animated fadeInUp" id="emoji-dropdown">' +
                '<div class="row m-0-l m-0-r">' +
                    '<div class="col-md-12">' +
                        '<h2 class="m-0-t">Emojis <i class="fa fa-times pull-right cursor-pointer closeEmoji"></i></h2>' +
                        '<input type="text" class="form-control" placeholder="Search..." id="emoji-filter"/>' +
                        '<br/>' +
                    '</div>' +
                '</div>' +
                '<div class="emoji-list">' +
                render(emojis) +
                '</div>' +
                '</div>').hide();

                this.$panel.appendTo('body');
            };

            this.destroy = function () {
                this.$panel.remove();
                this.$panel = null;
            };
        }
    });
}));
