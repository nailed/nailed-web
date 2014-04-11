package jk_5.nailed.web.webserver.irc

/**
 * No description given
 *
 * @author jk-5
 */
object ReplyCodes {

  //Error codes
  final val ERR_NOSUCHNICK = 401
  final val ERR_NOSUCHSERVER = 402
  final val ERR_NOSUCHCHANNEL = 403
  final val ERR_CANNOTSENDTOCHAN = 404
  final val ERR_TOOMANYCHANNELS = 405
  final val ERR_WASNOSUCHNICK = 406
  final val ERR_TOOMANYTARGETS = 407
  final val ERR_NOORIGIN = 409
  final val ERR_NORECIPIENT = 411
  final val ERR_NOTEXTTOSEND = 412
  final val ERR_NOTOPLEVEL = 413
  final val ERR_WILDTOPLEVEL = 414
  final val ERR_UNKNOWNCOMMAND = 421
  final val ERR_NOMOTD = 422
  final val ERR_NOADMININFO = 423
  final val ERR_FILEERROR = 424
  final val ERR_NONICKNAMEGIVEN = 431
  final val ERR_ERRONEUSNICKNAME = 432
  final val ERR_NICKNAMEINUSE = 433
  final val ERR_NICKCOLLISION = 436
  final val ERR_USERNOTINCHANNEL = 441
  final val ERR_NOTONCHANNEL = 442
  final val ERR_USERONCHANNEL = 443
  final val ERR_NOLOGIN = 444
  final val ERR_SUMMONDISABLED = 445
  final val ERR_USERSDISABLED = 446
  final val ERR_NOTREGISTERED = 451
  final val ERR_NEEDMOREPARAMS = 461
  final val ERR_ALREADYREGISTRED = 462
  final val ERR_NOPERMFORHOST = 463
  final val ERR_PASSWDMISMATCH = 464
  final val ERR_YOUREBANNEDCREEP = 465
  final val ERR_KEYSET = 467
  final val ERR_CHANNELISFULL = 471
  final val ERR_UNKNOWNMODE = 472
  final val ERR_INVITEONLYCHAN = 473
  final val ERR_BANNEDFROMCHAN = 474
  final val ERR_BADCHANNELKEY = 475
  final val ERR_NOPRIVILEGES = 481
  final val ERR_CHANOPRIVSNEEDED = 482
  final val ERR_CANTKILLSERVER = 483
  final val ERR_NOOPERHOST = 491
  final val ERR_UMODEUNKNOWNFLAG = 501
  final val ERR_USERSDONTMATCH = 502

  //Reply codes
  final val RPL_TRACELINK = 200
  final val RPL_TRACECONNECTING = 201
  final val RPL_TRACEHANDSHAKE = 202
  final val RPL_TRACEUNKNOWN = 203
  final val RPL_TRACEOPERATOR = 204
  final val RPL_TRACEUSER = 205
  final val RPL_TRACESERVER = 206
  final val RPL_TRACENEWTYPE = 208
  final val RPL_STATSLINKINFO = 211
  final val RPL_STATSCOMMANDS = 212
  final val RPL_STATSCLINE = 213
  final val RPL_STATSNLINE = 214
  final val RPL_STATSILINE = 215
  final val RPL_STATSKLINE = 216
  final val RPL_STATSYLINE = 218
  final val RPL_ENDOFSTATS = 219
  final val RPL_UMODEIS = 221
  final val RPL_STATSLLINE = 241
  final val RPL_STATSUPTIME = 242
  final val RPL_STATSOLINE = 243
  final val RPL_STATSHLINE = 244
  final val RPL_LUSERCLIENT = 251
  final val RPL_LUSEROP = 252
  final val RPL_LUSERUNKNOWN = 253
  final val RPL_LUSERCHANNELS = 254
  final val RPL_LUSERME = 255
  final val RPL_ADMINME = 256
  final val RPL_ADMINLOC1 = 257
  final val RPL_ADMINLOC2 = 258
  final val RPL_ADMINEMAIL = 259
  final val RPL_TRACELOG = 261
  final val RPL_NONE = 300
  final val RPL_AWAY = 301
  final val RPL_USERHOST = 302
  final val RPL_ISON = 303
  final val RPL_UNAWAY = 305
  final val RPL_NOWAWAY = 306
  final val RPL_WHOISUSER = 311
  final val RPL_WHOISSERVER = 312
  final val RPL_WHOISOPERATOR = 313
  final val RPL_WHOWASUSER = 314
  final val RPL_ENDOFWHO = 315
  final val RPL_WHOISIDLE = 317
  final val RPL_ENDOFWHOIS = 318
  final val RPL_WHOISCHANNELS = 319
  final val RPL_LISTSTART = 321
  final val RPL_LIST = 322
  final val RPL_LISTEND = 323
  final val RPL_CHANNELMODEIS = 324
  final val RPL_NOTOPIC = 331
  final val RPL_TOPIC = 332
  final val RPL_TOPICINFO = 333
  final val RPL_INVITING = 341
  final val RPL_SUMMONING = 342
  final val RPL_VERSION = 351
  final val RPL_WHOREPLY = 352
  final val RPL_NAMREPLY = 353
  final val RPL_LINKS = 364
  final val RPL_ENDOFLINKS = 365
  final val RPL_ENDOFNAMES = 366
  final val RPL_BANLIST = 367
  final val RPL_ENDOFBANLIST = 368
  final val RPL_ENDOFWHOWAS = 369
  final val RPL_INFO = 371
  final val RPL_MOTD = 372
  final val RPL_ENDOFINFO = 374
  final val RPL_MOTDSTART = 375
  final val RPL_ENDOFMOTD = 376
  final val RPL_YOUREOPER = 381
  final val RPL_REHASHING = 382
  final val RPL_TIME = 391
  final val RPL_USERSSTART = 392
  final val RPL_USERS = 393
  final val RPL_ENDOFUSERS = 394
  final val RPL_NOUSERS = 395

  //Reserved codes
  final val RPL_TRACECLASS = 209
  final val RPL_STATSQLINE = 217
  final val RPL_SERVICEINFO = 231
  final val RPL_ENDOFSERVICES = 232
  final val RPL_SERVICE = 233
  final val RPL_SERVLIST = 234
  final val RPL_SERVLISTEND = 235
  final val RPL_WHOISCHANOP = 316
  final val RPL_KILLDONE = 361
  final val RPL_CLOSING = 362
  final val RPL_CLOSEEND = 363
  final val RPL_INFOSTART = 373
  final val RPL_MYPORTIS = 384
  final val ERR_YOUWILLBEBANNED = 466
  final val ERR_BADCHANMASK = 476
  final val ERR_NOSERVICEHOST = 492
}
