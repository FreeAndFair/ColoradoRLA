SELECT ccd.description AS contest, 
        ccd.choice AS candidate, 
        count(ccic.cvr_contest_info_id) as votes
    FROM contest_choice_description AS ccd 
        LEFT JOIN ((contest AS c 
            LEFT JOIN cvr_contest_info AS cci
                ON c.id = cci.contest_id)
        LEFT JOIN 
            cvr_contest_info_choice as ccic 
            ON ccic.cvr_contest_info_id = cci.id)
            ON ccd.contest_id = c.id 
        AND ccd.choice = ccic.choice
    GROUP BY contest, candidate
    ORDER BY contest, candidate
;
