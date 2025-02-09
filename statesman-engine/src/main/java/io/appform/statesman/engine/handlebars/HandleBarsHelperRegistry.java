package io.appform.statesman.engine.handlebars;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.appform.statesman.engine.utils.StringUtils;
import io.appform.statesman.model.exception.ResponseCode;
import io.appform.statesman.model.exception.StatesmanError;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.Jsoup;


@Slf4j
public class HandleBarsHelperRegistry {

    private static final String DEFAULT_TIME_ZONE = "IST";
    private static final DecimalFormat decimalFormat = new DecimalFormat("######.##");
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private static final String EMPTY_STRING = "";
    private static final String POINTER = "pointer";

    private final Handlebars handlebars;

    private HandleBarsHelperRegistry(Handlebars handlebars) {
        this.handlebars = handlebars;
    }

    public static HandleBarsHelperRegistry newInstance(Handlebars handlebars) {
        return new HandleBarsHelperRegistry(handlebars);
    }

    public void register() {
        registerRupees();
        registerSuccess();
        registerStr();
        registerDecimalFormat();
        registerDate();
        registerRSub();
        registerToUpperCase();
        registerToLowerCase();
        registerDateFormat();
        registerToEpoch();
        registerStreq();
        registerStrNormalize();
        registerStrSingleLineText();
        registerStrNormalizeUpper();
        registerStrAlphaNumeric();
        registerStrNormalizeInitCap();
        registerCurrTime();
        registerElapsedTime();
        registerEq();
        registerLt();
        registerLte();
        registerGt();
        registerGte();
        registerSelectValuesGivenRange();
        registerMapLookup();
        registerMapLookupArray();
        registerMapArrLookup();
        registerMultiMapLookup();
        registerStrTranslate();
        registerStrTranslateArr();
        registerStrTranslateTxt();
        registerStrTranslateArrTxt();
        registerPhone();
        registerEmpty();
        registerNotEmpty();
        registerParseToInt();
        registerParseToIntPtr();
        registerHTML2Text();
        registerURLEncode();
        registerLocalTime();
        registerAdd();
        registerCountIf();
        registerCountMatchStr();
        registerTrimDecimalPoints();
        registerTrimDecimalPointsPtr();
    }

    private Object compareGte(int lhs) {
        return lhs >= 0
               ? "true"
               : null;
    }

    private Object compareGt(int lhs) {
        return lhs > 0
               ? "true"
               : null;
    }

    private Object compareLte(int lhs) {
        return lhs <= 0
               ? "true"
               : null;
    }

    private Object compareLt(int lhs) {
        return lhs < 0
               ? "true"
               : null;
    }

    private Object compareEq(int lhs) {
        return lhs == 0
               ? "true"
               : null;
    }

    private void registerGte() {
        handlebars.registerHelper("gte", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                return compareGte(Long.compare(aNumber.longValue(), (Long) option));
            }
            if (option instanceof Integer) {
                return compareGte(Integer.compare(aNumber.intValue(), (Integer) option));
            }
            if (option instanceof Double) {
                return compareGte(Double.compare(aNumber.doubleValue(), (Double) option));
            }
            if (option instanceof String) {
                return compareGte(Double.compare(aNumber.doubleValue(), Double.valueOf((String) option)));
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerSelectValuesGivenRange() {
        handlebars.registerHelper("selectValuesGivenRange", (Number aNumber, Options options) -> {
            val optionFrom = options.param(0);
            val optionTo = options.param(1);
            val optionTrue = options.param(2);
            val optionFalse = options.param(3);

            if ( aNumber == null ) return optionFalse;

            if (optionFrom instanceof Long && optionTo instanceof Long) {
                return (Long.compare(aNumber.longValue(), (Long) optionFrom) > 0 && Long.compare((Long) optionTo, aNumber.longValue()) > 0) ? optionTrue : optionFalse;
            }
            if (optionFrom instanceof Integer && optionTo instanceof Integer) {
                return (Integer.compare(aNumber.intValue(), (Integer) optionFrom) > 0 && Integer.compare((Integer) optionTo, aNumber.intValue()) > 0) ? optionTrue : optionFalse;
            }
            if (optionFrom instanceof Double && optionTo instanceof Double) {
                return (Double.compare(aNumber.doubleValue(), (Double) optionFrom) > 0 && Double.compare((Double) optionTo, aNumber.doubleValue()) > 0) ? optionTrue : optionFalse;
            }
            if (optionFrom instanceof String && optionTo instanceof String) {
                return (Double.compare(aNumber.doubleValue(), Double.valueOf((String) optionFrom)) > 0 && Double.compare(Double.valueOf((String) optionTo), aNumber.doubleValue()) > 0) ? optionTrue : optionFalse;
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerGt() {
        handlebars.registerHelper("gt", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                return compareGt(Long.compare(aNumber.longValue(), (Long) option));
            }
            if (option instanceof Integer) {
                return compareGt(Integer.compare(aNumber.intValue(), (Integer) option));
            }
            if (option instanceof Double) {
                return compareGt(Double.compare(aNumber.doubleValue(), (Double) option));
            }
            if (option instanceof String) {
                return compareGt(Double.compare(aNumber.doubleValue(), Double.valueOf((String) option)));
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerLte() {
        handlebars.registerHelper("lte", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                return compareLte(Long.compare(aNumber.longValue(), (Long) option));
            }
            if (option instanceof Integer) {
                return compareLte(Integer.compare(aNumber.intValue(), (Integer) option));
            }
            if (option instanceof Double) {
                return compareLte(Double.compare(aNumber.doubleValue(), (Double) option));
            }
            if (option instanceof String) {
                return compareLte(Double.compare(aNumber.doubleValue(), Double.valueOf((String) option)));
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }


    private void registerLt() {
        handlebars.registerHelper("lt", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                int lhs = Long.compare(aNumber.longValue(), (Long) option);
                return compareLt(lhs);
            }
            if (option instanceof Integer) {
                return compareLt(Integer.compare(aNumber.intValue(), (Integer) option));
            }
            if (option instanceof Double) {
                return compareLt(Double.compare(aNumber.doubleValue(), (Double) option));
            }
            if (option instanceof String) {
                return compareLt(Double.compare(aNumber.doubleValue(), Double.valueOf((String) option)));
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerEq() {
        handlebars.registerHelper("eq", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                int lhs = Long.compare(aNumber.longValue(), (Long) option);
                return compareEq(lhs);
            }
            if (option instanceof Integer) {
                return compareEq(Integer.compare(aNumber.intValue(), (Integer) option));
            }
            if (option instanceof Double) {
                return compareEq(Double.compare(aNumber.doubleValue(), (Double) option));
            }
            if (option instanceof String) {
                return compareEq(Double.compare(aNumber.doubleValue(), Double.valueOf((String) option)));
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerCurrTime() {
        handlebars.registerHelper("currTime", new Helper<Object>() {

            @Override
            public Object apply(Object startTime, Options options) throws IOException {
                return System.currentTimeMillis();
            }
        });
    }

    private void registerElapsedTime() {
        handlebars.registerHelper("elapsedTime", new Helper<String>() {

            @Override
            public Object apply(String dateFormat, Options options) throws IOException {
                if (null == options.params || options.params.length < 1) {
                    return 0;
                }
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                val fromData = options.param(0);
                Date fromDate = null;
                if (fromData instanceof String) {
                    try {
                        fromDate = sdf.parse((String) fromData);
                    }
                    catch (ParseException e) {
                        log.error("Error parsing from date: " + fromData, e);
                    }
                }
                if (null == fromDate) {
                    log.error("From date could not be extracted from {}", fromData);
                    return 0;
                }
                Date toDate = null;
                if (options.params.length > 1) {
                    val toData = options.param(1);
                    if (toData instanceof String) {
                        try {
                            toDate = sdf.parse((String) toData);
                        }
                        catch (ParseException e) {
                            log.error("Error parsing to date: " + toData, e);
                        }
                    }
                }
                if (null == toDate) {
                    toDate = new Date();
                }
                return toDate.getTime() - fromDate.getTime();
            }
        });
    }


    private void registerStreq() {
        handlebars.registerHelper("streq", (String value, Options options) -> {
            if (!Strings.isNullOrEmpty(value) && value.equals(options.param(0))) {
                return "true";
            }
            return null;
        });
    }

    private void registerAdd() {
        handlebars.registerHelper("add", (Number aNumber, Options options) -> {
            val option = options.param(0);
            if (option instanceof Long) {
                return aNumber.longValue() + (Long) option;
            }
            if (option instanceof Integer) {
                return aNumber.intValue() + (Integer) option;
            }
            if (option instanceof Double) {
                return aNumber.doubleValue() + (Double) option;
            }
            if (option instanceof String) {
                return aNumber.doubleValue() + Double.valueOf((String) option);
            }
            throw new StatesmanError(ResponseCode.OPERATION_NOT_SUPPORTED);
        });
    }

    private void registerCountIf() {
        handlebars.registerHelper("count_if", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {
                final String pointers = options.hash("pointers");
                if(Strings.isNullOrEmpty(pointers)) {
                    return "0";
                }
                val keys = pointers.split(",");
                if(keys.length == 0) {
                    return "0";
                }
                val count = Arrays.stream(keys)
                        .filter(keyElement -> {
                            val key = keyElement.trim();
                            final int lastIndex = lastIndex(options);
                            int value = lastIndex;
                            if (!Strings.isNullOrEmpty(key)) {
                                value = readString(node, key, lastIndex);
                            }
                            return options.hash("op_" + value);
                        })
                        .count();
                return Long.toString(count);
            }

        });
    }

    private void registerCountMatchStr() {
        handlebars.registerHelper("count_match_str", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {
                final String pointers = options.hash("pointers");
                if(Strings.isNullOrEmpty(pointers)) {
                    return "0";
                }
                val keys = pointers.split(",");
                if(keys.length == 0) {
                    return "0";
                }
                val count = Arrays.stream(keys)
                        .filter(keyElement -> {
                            String value = EMPTY_STRING;
                            val key = keyElement.trim();
                            if (!Strings.isNullOrEmpty(key)) {
                                val dataNode = node.at(key);
                                if (!(null == dataNode || dataNode.isNull() || dataNode.isMissingNode() || !dataNode.isValueNode())) {
                                    value = dataNode.asText();
                                }
                            }
                            return options.hash(normalizedKey(value), false);
                        })
                        .count();
                return Long.toString(count);
            }

        });
    }

    private void registerStrAlphaNumeric() {
        handlebars.registerHelper("alphaNumeric",
                                  (String value, Options options) -> StringUtils.alphaNumeric(value));
    }

    private void registerStrNormalize() {
        handlebars.registerHelper("normalize",
                                  (String value, Options options) -> StringUtils.normalize(value));
    }

    private void registerStrSingleLineText() {
        handlebars.registerHelper("singleLineText",
                                  (String value, Options options) -> StringUtils.removeNewLineAndTabs(value));
    }

    private void registerStrNormalizeUpper() {
        handlebars.registerHelper("normalize_upper",
                                  (String value, Options options) -> StringUtils.normalize(value).toUpperCase());
    }

    private void registerStrNormalizeInitCap() {
        handlebars.registerHelper("normalize_init_cap",
                                  (String value, Options options) -> StringUtils.normalizeInitCap(value));
    }

    private void registerDateFormat() {
        handlebars.registerHelper("dateFormat", (Long context, Options options) -> {
            try {
                if (null != context) {
                    SimpleDateFormat sdf = new SimpleDateFormat(options.param(0));
                    String timeZone =
                            options.params.length < 2
                            ? DEFAULT_TIME_ZONE
                            : options.param(1);
                    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
                    return sdf.format(context);
                }
            }
            catch (Exception e) {
                log.error("Error formatting date", e);
            }
            return null;
        });
    }

    private void registerToEpoch() {
        handlebars.registerHelper("toEpochTime", (String context, Options options) -> {
            try {
                if (null == options.params || options.params.length == 0) {
                    return 0L;
                }
                if (null != context) {
                    SimpleDateFormat sdf = new SimpleDateFormat(options.param(0));
                    String timeZone =
                            options.params.length < 2
                            ? DEFAULT_TIME_ZONE
                            : options.param(1);
                    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
                    return sdf.parse(context).getTime();
                }
            }
            catch (Exception e) {
                log.error("Error formatting date", e);
            }
            return 0L;
        });
    }

    private void registerToLowerCase() {
        handlebars.registerHelper("toLowerCase", (String context, Options options) -> {
            if (!Strings.isNullOrEmpty(context)) {
                return context.toLowerCase().trim();
            }
            return null;
        });
    }

    private void registerToUpperCase() {
        handlebars.registerHelper("toUpperCase", (String context, Options options) -> {
            if (!Strings.isNullOrEmpty(context)) {
                return context.toUpperCase().trim();
            }
            return null;
        });
    }

    private void registerRSub() {
        handlebars.registerHelper("rsub", (String context, Options options) -> {
            if (!Strings.isNullOrEmpty(context)) {
                int length = context.length();
                Integer index = options.param(0);
                if (length >= index) {
                    return context.substring(length - index);
                }
                return context;
            }
            return null;
        });
    }

    private void registerDate() {
        handlebars.registerHelper("date", (Long context, Options options) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            try {
                if (null != context) {
                    return sdf.format(new Date(context));
                }
            }
            catch (Exception e) {
                log.error("Error converting date", e);
            }
            return sdf.format(new Date());
        });
    }

    private void registerDecimalFormat() {
        handlebars.registerHelper("decimalFormat", (Number aNumber, Options options) -> {
            DecimalFormat decimalFormatReqd =
                    (options.params.length > 0 && !Strings.isNullOrEmpty(options.param(0)))
                    ? new DecimalFormat(options.param(0))
                    : decimalFormat;
            return decimalFormatReqd.format(aNumber.doubleValue());
        });
    }

    private void registerStr() {
        handlebars.registerHelper("str",
                                  (Number aNumber, Options options) -> String.valueOf(aNumber.doubleValue()));
    }

    private void registerSuccess() {
        handlebars.registerHelper("success", (Boolean context, Options options) -> context
                                                                                     ? "succeeded"
                                                                                     : "failed");
    }

    private void registerRupees() {
        handlebars.registerHelper("rupees", (Number aNumber, Options options) -> {
            double value = 0;
            if (aNumber instanceof Long) {
                value = aNumber.longValue();
            }
            if (aNumber instanceof Integer) {
                value = aNumber.intValue();
            }
            if (aNumber instanceof Double) {
                value = aNumber.doubleValue();
            }
            return decimalFormat.format(value / 100.0);
        });
    }

    private void registerMapLookup() {
        handlebars.registerHelper("map_lookup", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {
                final String key = options.hash(POINTER);
                final int lastIndex = lastIndex(options);
                int value = lastIndex;
                if (!Strings.isNullOrEmpty(key)) {
                    value = readString(node, key, lastIndex);
                }
                return singleElement(options, value);
            }

            private CharSequence singleElement(Options options, int value) throws JsonProcessingException {
                return MAPPER.writeValueAsString(options.hash("op_" + value));
            }

        });
    }

    private int readString(JsonNode node, String key, int lastIndex) {
        int value = lastIndex;
        val keyNode = node.at(key);
        if (keyNode.isTextual()) {
            value = extractOptionValue(keyNode, lastIndex);
        }
        if (keyNode.isIntegralNumber()) {
            value = keyNode.asInt(lastIndex);
        }
        return Math.min(value, lastIndex);
    }

    private void registerMapLookupArray() {
        handlebars.registerHelper("map_lookup_arr", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {
                final String key = options.hash(POINTER);
                final int lastIndex = lastIndex(options);
                List<Integer> indices = new ArrayList<>();
                if (Strings.isNullOrEmpty(key)) {
                    log.warn("Invalid json node. Defaulting to array of last index: {} for empty key", lastIndex);
                    indices.add(lastIndex);
                }
                else {
                    val keyNode = node.at(key);
                    if (!keyNode.isArray()) {
                        readNode(lastIndex, indices, keyNode);
                    }
                    else {
                        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                                keyNode.elements(), Spliterator.ORDERED), false)
                                .forEach(childNode -> readNode(lastIndex, indices, childNode));
                    }
                }                //Array of options
                return MAPPER.writeValueAsString(
                        indices.stream()
                                .map(i -> options.hash("op_" + i))
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList()));
            }

            private void readNode(int lastIndex, List<Integer> indices, JsonNode keyNode) {
                int value;
                if (keyNode.isTextual()) {
                    value = extractOptionValue(keyNode, lastIndex);
                }
                else if (keyNode.isIntegralNumber()) {
                    value = keyNode.asInt(lastIndex);
                }
                else {
                    value = lastIndex;
                }
                if (value < 10) {
                    //Single option -> return one value
                    indices.add(Math.min(value, lastIndex));
                }
                else {
                    //Multiple concatenated options
                    List<Integer> revIndices = new ArrayList<>();
                    while (value > 0) {
                        revIndices.add(Math.min(value % 10, lastIndex));
                        value = value / 10;
                    }
                    Collections.reverse(revIndices);
                    indices.addAll(revIndices);
                }
            }
        });
    }

    /**
     * Example Usage:
     *        JsonNode obj <- {"state" : "karnataka","language" : 2 }
     *        defaults to english under error cases
     *        Template template = handlebars.compileInline("{{map_arr_lookup array='english,hindi,tamil,bengali,kannada' op_karnataka='2,3,1' op_tamilnadu='3,2,1' key1='/state' key2='/language'}}" );
     */
    private void registerMapArrLookup() {
        handlebars.registerHelper("map_arr_lookup", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                final String key = options.hash("array");
                if (Strings.isNullOrEmpty(key)) {
                    return empty();
                }

                String[] res = key.split(",");
                if (res.length == 1) {
                    return res[0];
                }

                final String key1 = options.hash("key1");
                val key1Data = node.at(key1);
                if (null == key1Data || key1Data.isNull() || !key1Data.isTextual()) {
                    return res[0];
                }
                String key1String = key1Data.asText();

                final String key2 = options.hash("key2");
                val key2Value = node.at(key2);
                if (null == key2Value || key2Value.isNull()) {
                    return res[0];
                }

                final String arrayOrderCsv = options.hash("op_" + key1String);
                if ( null == arrayOrderCsv || arrayOrderCsv.isEmpty() ) {
                    return res[0];
                }

                String[] arrayOrder = arrayOrderCsv.split(",");
                if (key2Value.asInt() > 0 && key2Value.asInt() <= arrayOrder.length) {
                    String arrayLookupIndexStr = arrayOrder[key2Value.asInt() - 1];
                    int arrayLookupIndex = Integer.parseInt(arrayLookupIndexStr);
                    if ( arrayLookupIndex > 1 && arrayLookupIndex <= res.length ) {
                        return (res[arrayLookupIndex - 1]);
                    }
                }

                return res.length > 0 ? res[0] : empty();
            }

            private CharSequence empty() {
                return EMPTY_STRING;
            }
        });
    }

    /**
      * Example Usage:
      *          JsonNode obj <- {"state" : "KA","language" : 41 }
      *          defaults to english under error cases
      *       Template template = handlebars.compileInline("{{map_arr_lookup default='english' op_ka='1:kannada,2:hindi,3:english,41:tamil,42:telugu,43:malayalam' op_tn='3:tamil' key1='/state' key2='/language'}}" );
      */
    private void registerMultiMapLookup() {
        handlebars.registerHelper("multi_map_lookup", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                String defFeched = options.hash("default");
                final String defaultValue = ( null == defFeched || defFeched.isEmpty() ) ? EMPTY_STRING
                    : defFeched;

                final String key1 = options.hash("key1");
                if ( null == key1 || key1.isEmpty() ) return defaultValue;
                val key1Data = node.at(key1);
                if (null == key1Data || key1Data.isNull() || !key1Data.isTextual()) return defaultValue;
                String key1String = normalizedKey(key1Data.asText());

                final String key2 = options.hash("key2");
                if ( null == key2 || key2.isEmpty() ) return defaultValue;
                val key2Data = node.at(key2);
                if (null == key2Data || key2Data.isNull() ) return defaultValue;
                String key2String = key2Data.asText();

                Map<String,String> kvMap = Maps.newHashMap();
                {
                    final String kvPairs = options.hash(key1String);
                    if (null == kvPairs || kvPairs.isEmpty()) return defaultValue;

                    String[] pairs = kvPairs.split(",");

                    for (String pair : pairs) {
                        String[] tuple = pair.split(":");
                        if (tuple.length == 2) {
                            kvMap.put(tuple[0], tuple[1]);
                        }
                    }
                }

                if ( kvMap.containsKey(key2String) ) {
                    return kvMap.get(key2String);
                }

                return defaultValue;
            }
        });
    }

    private void registerStrTranslate() {
        handlebars.registerHelper("translate", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                final String key = options.hash(POINTER);
                if (Strings.isNullOrEmpty(key)) {
                    return empty();
                }
                val dataNode = node.at(key);
                if (null == dataNode || dataNode.isNull() || dataNode.isMissingNode() || !dataNode.isValueNode()) {
                    return empty();
                }
                val lookupKey = dataNode.asText();
                if (Strings.isNullOrEmpty(lookupKey)) {
                    return empty();
                }
                val lookupValue = options.hash(normalizedKey(lookupKey));
                if (null == lookupValue) {
                    return empty();
                }
                return MAPPER.writeValueAsString(lookupValue);
            }

            private CharSequence empty() throws JsonProcessingException {
                return MAPPER.writeValueAsString(NullNode.getInstance());
            }
        });
    }

    private void registerStrTranslateArr() {
        handlebars.registerHelper("translate_arr", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                final String key = options.hash(POINTER);
                if (Strings.isNullOrEmpty(key)) {
                    return empty();
                }
                val dataNode = node.at(key);
                if (null == dataNode || dataNode.isNull() || dataNode.isMissingNode()
                        || (!dataNode.isValueNode() && !dataNode.isArray())) {
                    return empty();
                }
                val lookupKeys = new ArrayList<String>();
                if (dataNode.isValueNode()) {
                    val lookupKey = dataNode.asText();
                    if (Strings.isNullOrEmpty(lookupKey)) {
                        return empty();
                    }
                    lookupKeys.add(lookupKey);
                }
                else if (dataNode.isArray()) {
                    lookupKeys.addAll(StreamSupport.stream(Spliterators.spliteratorUnknownSize(dataNode.elements(),
                                                                                               Spliterator.ORDERED),
                                                           false)
                                              .filter(child -> !child.isNull() && !child.isMissingNode() && child.isValueNode())
                                              .map(JsonNode::asText)
                                              .collect(Collectors.toList()));

                }
                return MAPPER.writeValueAsString(lookupKeys.stream()
                                                         .map(lookupKey -> {
                                                             val value = options.hash(normalizedKey(lookupKey));
                                                             return null == value
                                                                    ? NullNode.getInstance()
                                                                    : value;
                                                         })
                                                         .filter(Objects::nonNull)
                                                         .collect(Collectors.toList()));
            }

            private CharSequence empty() throws JsonProcessingException {
                return MAPPER.writeValueAsString(MAPPER.createArrayNode());
            }
        });
    }

    private void registerStrTranslateTxt() {
        handlebars.registerHelper("translate_txt", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                final String key = options.hash(POINTER);
                if (Strings.isNullOrEmpty(key)) {
                    return empty();
                }
                val dataNode = node.at(key);
                if (null == dataNode || dataNode.isNull() || dataNode.isMissingNode() || !dataNode.isValueNode()) {
                    return empty();
                }
                val lookupKey = dataNode.asText();
                if (Strings.isNullOrEmpty(lookupKey)) {
                    return empty();
                }
                val lookupValue = options.hash(normalizedKey(lookupKey));
                if (null == lookupValue) {
                    return empty();
                }
                return lookupValue.toString();
            }

            private CharSequence empty() {
                return EMPTY_STRING;
            }
        });
    }

    private void registerStrTranslateArrTxt() {
        handlebars.registerHelper("translate_arr_txt", new Helper<JsonNode>() {
            @Override
            public CharSequence apply(JsonNode node, Options options) throws IOException {

                final String key = options.hash(POINTER);
                if (Strings.isNullOrEmpty(key)) {
                    return empty();
                }
                val dataNode = node.at(key);
                if (null == dataNode || dataNode.isNull() || dataNode.isMissingNode()
                        || (!dataNode.isValueNode() && !dataNode.isArray())) {
                    return empty();
                }
                val lookupKeys = new ArrayList<String>();
                if (dataNode.isValueNode()) {
                    val lookupKey = dataNode.asText();
                    if (Strings.isNullOrEmpty(lookupKey)) {
                        return empty();
                    }
                    lookupKeys.add(lookupKey);
                }
                else if (dataNode.isArray()) {
                    lookupKeys.addAll(StreamSupport.stream(Spliterators.spliteratorUnknownSize(dataNode.elements(),
                                                                                               Spliterator.ORDERED),
                                                           false)
                                              .filter(child -> !child.isNull() && !child.isMissingNode() && child.isValueNode())
                                              .map(JsonNode::asText)
                                              .collect(Collectors.toList()));

                }
                return lookupKeys.stream()
                        .map(lookupKey -> {
                            val value = options.hash(normalizedKey(lookupKey));
                            return null == value
                                   ? null
                                   : value.toString();
                        })
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.joining(", "));
            }

            private CharSequence empty() {
                return EMPTY_STRING;
            }
        });
    }

    private void registerPhone() {
        handlebars.registerHelper("phone", (String context, Options options) -> {
            if (Strings.isNullOrEmpty(context)) {
                return null;
            }
            final String numericString = numericStr(context);
            if (Strings.isNullOrEmpty(numericString)) {
                return null;
            }
            if (numericString.length() == 10) {
                return numericString;
            }
            return numericString.length() > 10
                   ? numericString.substring(numericString.length() - 10)
                   : null;
        });
    }

    private void registerEmpty() {
        handlebars.registerHelper("empty", new Helper<JsonNode>() {
            @Override
            public Object apply(JsonNode context, Options options) throws IOException {
                if (context.isNull() || context.isMissingNode()) {
                    return true;
                }
                if (context.size() == 0) {
                    return true;
                }

                if (context.isArray()) {
                    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(context.elements(),
                                                                                    Spliterator.ORDERED), false)
                            .allMatch(node -> node.isNull() || node.isMissingNode() || (node.isTextual() && Strings.isNullOrEmpty(
                                    node.asText())));
                }
                return false;
            }
        });
    }

    private void registerNotEmpty() {
        handlebars.registerHelper("notEmpty", new Helper<JsonNode>() {
            @Override
            public Object apply(JsonNode context, Options options) throws IOException {
                if (context.isNull() || context.isMissingNode()) {
                    return false;
                }
                if (context.size() == 0) {
                    return false;
                }

                if (context.isArray()) {
                    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(context.elements(),
                                                                                    Spliterator.ORDERED), false)
                            .noneMatch(node -> node.isNull() || node.isMissingNode() || (node.isTextual() && Strings.isNullOrEmpty(
                                    node.asText())));
                }
                return true;
            }
        });
    }

    private void registerParseToInt() {
        handlebars.registerHelper("toInt", new Helper<String>() {
            @Override
            public Object apply(String context, Options options) throws IOException {
                if (!Strings.isNullOrEmpty(context)) {
                    try {
                        return Integer.parseInt(numericStr(context));
                    }
                    catch (NumberFormatException e) {
                        log.error("Count not parse string value: {}", context);
                    }
                }
                return -1;
            }
        });
    }



    private void registerParseToIntPtr() {
        handlebars.registerHelper("toIntPtr", new Helper<JsonNode>() {
            @Override
            public Object apply(JsonNode node, Options options) throws IOException {
                final String pointer = options.hash(POINTER);
                if (!Strings.isNullOrEmpty(pointer) && null != node && !node.isNull() && !node.isMissingNode()) {
                    val intNode = node.at(pointer);
                    if (intNode.isIntegralNumber()) {
                        return intNode.asLong();
                    }
                    if (intNode.isTextual()) {
                        try {
                            return Integer.parseInt(numericStr(intNode.asText()));
                        }
                        catch (NumberFormatException e) {
                            log.error("Count not parse value: {} at: {}", node, pointer);
                        }
                    }
                }
                return -1;
            }
        });
    }

    private void registerTrimDecimalPoints() {
        handlebars.registerHelper("trimDecimalPoints", (String context, Options options) -> {
            try {
                return trimDecimalPoints(context);
            } catch (Exception e) {
                log.error("Exception occurred while removing decimal points", e);
            }
            return "-1";
        });
    }

    private void registerTrimDecimalPointsPtr() {
        handlebars.registerHelper("trimDecimalPointsPtr", (JsonNode node, Options options) -> {
            try {
                final String pointer = options.hash(POINTER);
                if (!Strings.isNullOrEmpty(pointer) && null != node && !node.isNull() && !node.isMissingNode()) {
                    JsonNode contextNode = node.at(pointer);
                    String context =
                        contextNode == null || contextNode.isMissingNode() || contextNode.isNull()
                            ? null : contextNode.asText();
                    return trimDecimalPoints(context);
                }
            } catch (Exception e) {
                log.error("Exception occurred while removing decimal points", e);
            }
            return "-1";
        });
    }

    private Object trimDecimalPoints(String context) {
        if (!Strings.isNullOrEmpty(context)) {
            if (context.contains(".")) {
                return context.substring(0, context.indexOf("."));
            } else {
                return context;
            }
        }
        return "-1";
    }

    private void registerHTML2Text() {
        handlebars.registerHelper("html2Text", new Helper<String>() {
            @Override
            public CharSequence apply(String htmlString, Options options) throws IOException {
                final String text = Jsoup.parse(htmlString).text();
                return Strings.isNullOrEmpty(text)
                       ? EMPTY_STRING
                       : text.replace("\\n", EMPTY_STRING).trim();
            }
        });
    }

    private void registerURLEncode() {
        handlebars.registerHelper("URLEncode", new Helper<String>() {
            @Override
            public CharSequence apply(String str, Options options) throws IOException {
                return URLEncoder.encode(str,"UTF-8");

            }
        });
    }

    private void registerLocalTime() {
        handlebars.registerHelper("localTime", new Helper<String>() {
            @Override
            public CharSequence apply(String tz, Options options) throws IOException {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(tz));
                return MAPPER.writeValueAsString(MAPPER.createObjectNode()
                                                         .set("localTime", MAPPER.createObjectNode()
                                                                 .put("hour", now.getHour())
                                                                 .put("minutes", now.getMinute())
                                                                 .put("seconds", now.getSecond())));
            }
        });
    }

    private int extractOptionValue(JsonNode keyNode, int defaultValue) {
        final String text = keyNode.asText();
        try {
            return Strings.isNullOrEmpty(text)
                   ? defaultValue
                   : Integer.parseInt(numericStr(text));
        }
        catch (NumberFormatException e) {
            log.error("Error parsing number text: " + text, e);
            return defaultValue;
        }
    }

    private String numericStr(String context) {
        return context.replaceAll("[^\\p{Digit}]", EMPTY_STRING);
    }

    private int lastIndex(Options options) {
        return options.hash.size() - 1;
    }

    private String normalizedKey(String s) {
        return "op_" + StringUtils.normalize(s);
    }

}
