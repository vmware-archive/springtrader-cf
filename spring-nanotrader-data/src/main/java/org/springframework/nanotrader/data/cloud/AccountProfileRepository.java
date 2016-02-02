/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.data.cloud;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.nanotrader.data.domain.Accountprofile;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface AccountProfileRepository {

    @RequestLine("GET /profiles/?userId={userId}&passwd={passwd}")
    List<Accountprofile> findByUseridAndPasswd(
            @Param(value = "userId") String userId,
            @Param(value = "passwd") String passwd);

    @RequestLine("GET /profiles/?userId={userId}")
    List<Accountprofile> findByUserid(
            @Param(value = "userId") String userId);

    @RequestLine("GET /profiles/?authToken={authtoken}")
    List<Accountprofile> findByAuthtoken(
            @Param(value = "authtoken") String authtoken);

    @RequestLine("GET /profiles/{id}")
    Accountprofile findOne(@Param(value = "id") Long id);

    @RequestLine("GET /profiles/{id}/accounts")
    List<Account> findAccounts(@Param(value = "id") Long id);

    @RequestLine("POST /profiles/")
    @Headers("Content-Type: application/json")
    Accountprofile save(@RequestBody Accountprofile profile);

    @RequestLine("DELETE /profiles/")
    @Headers("Content-Type: application/json")
    Accountprofile delete(@RequestBody Accountprofile profile);
}
