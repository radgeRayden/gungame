fn tolower (str)
    using import String

    delta := char"a" - char"A"
    local result : String
    for c in str
        if (c >= char"A" and c <= char"Z")
            'append result (c + delta)
        else
            'append result c
    result

inline match-string-enum (enum-type value)
    using import hash
    using import switcher
    using import print

    call
        switcher sw
            va-map
                inline (fT)
                    case (static-eval (hash (tolower (fT.Name as string))))
                        getattr enum-type fT.Name
                enum-type.__fields__
            default
                error "unknown enum value"
        hash (tolower value)

inline record-matcher (T data)
    using import switcher slice String

    inline kv-pair (datum)
        head tail := decons datum
        _ (head as Symbol) (tail @ 1)

    local result : T
    for field in data
        call
            switcher sw
                va-map
                    inline (fT)
                        fT := fT.Type
                        field-name := keyof fT
                        T := unqualified fT
                        case field-name
                            value := switcher-context...
                            let value =
                                static-if (T < CEnum)
                                    match-string-enum T (value as Symbol as string)
                                elseif (T < integer)
                                    (sc_const_int_extract value) as T
                                elseif (T < real)
                                    (sc_const_real_extract value) as T
                                elseif (T == String)
                                    String (value as string)

                            (getattr result field-name) = value
                    T.__fields__
                default ()
            kv-pair (field as list)
    result

do
    let record-matcher
    local-scope;
