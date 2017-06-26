/*
 * Copyright 2017 Paolo Villadarez
 *
 * This code cannot be used, copied, or redistributed without express consent from the
 * author. Please contact villadarez@gmail.com for permission to use this code.
 *
 *
 * Ver  Date        Change Log
 * ---  ----------  -----------------------------------
 * 1.0  2017-06-14  Initial version
 */
package ca.humanheartnature.quickbooks.comm;

import ca.humanheartnature.abstracts.comm.DataServiceSingleton;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.Config;

/**
 * HTTP service used as an interface for exchanging data with QBO
 */
public class QboDataServiceSingleton implements DataServiceSingleton
{
   /** Version of QBO API this module was developed/tested in */
   private final String QBO_API_VERSION = "9";
   
   /** Holds connection details to QBO */
   private final DataService dataService;
   
   /**
    * @param baseUrl URL to QBO
    * @param appToken Specifies the QBO account to connect to
    * @param realmId Realm ID
    * @param consumerKey OAuth Key
    * @param consumerSecret OAuth Secret
    * @param accessToken Token token 3rd leg of authentication
    * @param accessSecret Secret token for 3rd leg of authentication
    * @throws FMSException 
    */
   public QboDataServiceSingleton(String baseUrl,
                                  String appToken,
                                  String realmId,
                                  String consumerKey,
                                  String consumerSecret,
                                  String accessToken,
                                  String accessSecret) throws FMSException
   {
      if (baseUrl == null ||
          appToken == null ||
          realmId == null ||
          consumerKey == null ||
          consumerSecret == null ||
          accessToken == null ||
          accessSecret == null)
      {
         throw new IllegalArgumentException("Arguments cannot be null");
      }
      
      OAuthAuthorizer authorizer =
            new OAuthAuthorizer(consumerKey, consumerSecret, accessToken, accessSecret);
      Context context = new Context(authorizer, ServiceType.QBO, realmId);
      Config.setProperty(Config.BASE_URL_QBO, baseUrl);
      context.setMinorVersion(QBO_API_VERSION);
      dataService = new DataService(context);
   }

   @Override
   public DataService getInstance() 
   {
      return dataService;
   }
   
}
